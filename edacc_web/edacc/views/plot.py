# -*- coding: utf-8 -*-
"""
    edacc.views.plot
    ----------------

    Plot view functions.

    The handlers defined in this module return the plotted images as
    HTTP responses.

    :copyright: (c) 2010 by Daniel Diepold.
    :license: MIT, see LICENSE for details.
"""

import os
import numpy
import StringIO
import csv

from flask import Module, render_template as render
from flask import Response, abort, request, g
from werkzeug import Headers

from edacc import plots, config, models
from sqlalchemy.orm import joinedload
from edacc.views.helpers import require_phase, require_login

plot = Module(__name__)


def scatter_2solver_1property_points(db, exp, sc1, sc2, instances, solver_property, run):
    results1 = db.session.query(db.ExperimentResult)
    results1.enable_eagerloads(True).options(joinedload(db.ExperimentResult.instance, db.ExperimentResult.solver_configuration))
    results1.options(joinedload(db.ExperimentResult.solver_properties), joinedload(db.ExperimentResult.instance))
    results1 = results1.filter_by(experiment=exp, solver_configuration=sc1)

    results2 = db.session.query(db.ExperimentResult)
    results2.enable_eagerloads(True).options(joinedload(db.ExperimentResult.instance, db.ExperimentResult.solver_configuration))
    results2.options(joinedload(db.ExperimentResult.solver_properties), joinedload(db.ExperimentResult.instance))
    results2 = results2.filter_by(experiment=exp, solver_configuration=sc2)

    points = []
    if run == 'average':
        for instance in instances:
            s1_avg = numpy.average([j.get_property_value(solver_property, db) for j in results1.filter_by(instance=instance).all()])
            s2_avg = numpy.average([j.get_property_value(solver_property, db) for j in results2.filter_by(instance=instance).all()])
            points.append((s1_avg, s2_avg, instance))
    elif run == 'median':
        for instance in instances:
            x = numpy.median([j.get_property_value(solver_property, db) for j in results1.filter_by(instance=instance).all()])
            y = numpy.median([j.get_property_value(solver_property, db) for j in results2.filter_by(instance=instance).all()])
            points.append((x, y, instance))
    elif run == 'all':
        for instance in instances:
            xs = [j.get_property_value(solver_property, db) for j in results1.filter_by(instance=instance).all()]
            ys = [j.get_property_value(solver_property, db) for j in results2.filter_by(instance=instance).all()]
            points += zip(xs, ys, [instance] * len(xs))
    else:
        for instance in instances:
            r1 = results1.filter_by(instance=instance, run=int(run)).first()
            r2 = results2.filter_by(instance=instance, run=int(run)).first()
            points.append((
                r1.get_property_value(solver_property, db),
                r2.get_property_value(solver_property, db),
                instance
            ))

    return points


@plot.route('/<database>/experiment/<int:experiment_id>/scatter-plot-1property/')
@require_phase(phases=(5, 6, 7))
@require_login
def scatter_2solver_1property(database, experiment_id):
    """ Returns an image with a scatter plot of the result property of two
        solver configurations' results on instances as HTTP response.

        The data to be plotted has to be specified as GET parameters:

        solver_config1: id of the first solver configuration
        solver_config2: id of the second solver configuratio
        instances: id of an instance, multiple occurences allowed.
        run: 'average', 'median', 'all', or an integer of the run.
                If the value is 'all', all runs of the solvers will be plotted.
                If the value is 'average' or 'median', these values will be calculated
                across multiple runs of one solver on an instance.
                If the value is an integer, the data of this specific run is used.
        solver_property: id of a solver property (SolverProperty table) or the special case
                         'cputime' for the time column of the ExperimentResult table.
    """
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    s1 = int(request.args['solver_config1'])
    s2 = int(request.args['solver_config2'])
    instances = [db.session.query(db.Instance).filter_by(idInstance=int(id)).first() for id in request.args.getlist('instances')]
    run = request.args['run']
    xscale = request.args['xscale']
    yscale = request.args['yscale']
    solver_property = request.args['solver_property']
    if solver_property != 'cputime':
        solver_prop = db.session.query(db.SolverProperty).get(int(solver_property))

    sc1 = db.session.query(db.SolverConfiguration).get(s1) or abort(404)
    sc2 = db.session.query(db.SolverConfiguration).get(s2) or abort(404)

    points = scatter_2solver_1property_points(db, exp, sc1, sc2, instances, solver_property, run)

    max_x = max([p[0] for p in points] or [0])
    max_y = max([p[1] for p in points] or [0])
    max_x = max_y = max(max_x, max_y) * 1.1

    title = sc1.solver.name + ' vs. ' + sc2.solver.name
    if solver_property == 'cputime':
        xlabel = sc1.solver.name + ' CPU Time'
        ylabel = sc2.solver.name + ' CPU Time'
    else:
        xlabel = sc1.solver.name + ' ' + solver_prop.name
        ylabel = sc2.solver.name + ' ' + solver_prop.name

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Instance', xlabel, ylabel])
        for x, y, i in points:
            csv_writer.writerow([str(i), x, y])
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    elif request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.pdf'
        plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, format='pdf', xscale=xscale, yscale=yscale, diagonal_line=True)
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename=sc1.solver.name + '_vs_' + sc2.solver.name + '.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.png'
        pts = plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, xscale=xscale, yscale=yscale, diagonal_line=True)
        points = [(pts[i][0], pts[i][1], points[i][0], points[i][1], points[i][2]) for i in xrange(len(points))]
        if request.args.has_key('imagemap'):
            return render('/analysis/imagemap_2solver_1property.html', database=database, points=points, sc1=sc1, sc2=sc2)
        else:
            response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


def scatter_1solver_instance_vs_result_property_points(db, exp, solver_config, instances, instance_property, solver_property, run):
    results = db.session.query(db.ExperimentResult)
    results.enable_eagerloads(True).options(joinedload(db.ExperimentResult.instance, db.ExperimentResult.solver_configuration))
    results = results.filter_by(experiment=exp, solver_configuration=solver_config)

    points = []
    if run == 'average':
        for instance in instances:
            prop_value = instance.get_property_value(instance_property, db)
            s_avg = numpy.average([j.get_property_value(solver_property, db) for j in results.filter_by(instance=instance).all()])
            points.append((prop_value, s_avg, instance))
    elif run == 'median':
        for instance in instances:
            prop_value = instance.get_property_value(instance_property, db)
            y = numpy.median([j.get_property_value(solver_property, db) for j in results.filter_by(instance=instance).all()])
            points.append((prop_value, y, instance))
    elif run == 'all':
        for instance in instances:
            prop_value = instance.get_property_value(instance_property, db)
            xs = [prop_value] * len(results.filter_by(instance=instance).all())
            ys = [j.get_property_value(solver_property, db) for j in results.filter_by(instance=instance).all()]
            points += zip(xs, ys, [instance] * len(xs))
    else:
        for instance in instances:
            res = results.filter_by(instance=instance, run=int(run)).first()
            points.append((
                instance.get_property_value(instance_property, db),
                res.get_property_value(solver_property, db),
                instance
            ))

    return points


@plot.route('/<database>/experiment/<int:experiment_id>/scatter-plot-instance-vs-result/')
@require_phase(phases=(5, 6, 7))
@require_login
def scatter_1solver_instance_vs_result_property(database, experiment_id):
    """ description """
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    solver_config = int(request.args['solver_config'])
    run = request.args['run']
    xscale = request.args['xscale']
    yscale = request.args['yscale']
    solver_property = request.args['solver_property']
    instance_property = request.args['instance_property']

    instances = [db.session.query(db.Instance).filter_by(idInstance=int(id)).first() for id in request.args.getlist('instances')]

    if solver_property != 'cputime':
        solver_prop = db.session.query(db.SolverProperty).get(int(solver_property))

    instance_prop = db.session.query(db.InstanceProperty).get(instance_property)

    solver_config = db.session.query(db.SolverConfiguration).get(solver_config) or abort(404)

    points = scatter_1solver_instance_vs_result_property_points(db, exp, solver_config, instances, instance_property, solver_property, run)

    xlabel = instance_prop.name

    if solver_property == 'cputime':
        ylabel = 'CPU Time'
    else:
        ylabel = solver_prop.name

    title = str(solver_config)

    max_x = max([p[0] for p in points] or [0]) * 1.1
    max_y = max([p[1] for p in points] or [0]) * 1.1

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Instance', xlabel, ylabel])
        for x, y, i in points:
            csv_writer.writerow([str(i), x, y])
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    elif request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.pdf'
        plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, format='pdf', xscale=xscale, yscale=yscale)
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename=str(solver_config) + '.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.png'
        pts = plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, xscale=xscale, yscale=yscale)
        points = [(pts[i][0], pts[i][1], points[i][0], points[i][1], points[i][2]) for i in xrange(len(points))]
        if request.args.has_key('imagemap'):
            return render('/analysis/imagemap_instance_vs_result.html', database=database, points=points, sc=solver_config)
        else:
            response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


def scatter_1solver_result_vs_result_property_plot(db, exp, solver_config, instances, solver_property1, solver_property2, run):
    results = db.session.query(db.ExperimentResult)
    results.enable_eagerloads(True).options(joinedload(db.ExperimentResult.instance, db.ExperimentResult.solver_configuration))
    results = results.filter_by(experiment=exp, solver_configuration=solver_config)

    points = []
    if run == 'average':
        for instance in instances:
            s1_avg = numpy.average([j.get_property_value(solver_property1, db) for j in results.filter_by(instance=instance).all()])
            s2_avg = numpy.average([j.get_property_value(solver_property2, db) for j in results.filter_by(instance=instance).all()])
            points.append((s1_avg, s2_avg, instance))
    elif run == 'median':
        for instance in instances:
            x = numpy.median([j.get_property_value(solver_property1, db) for j in results.filter_by(instance=instance).all()])
            y = numpy.median([j.get_property_value(solver_property2, db) for j in results.filter_by(instance=instance).all()])
            points.append((x, y, instance))
    elif run == 'all':
        for instance in instances:
            xs = [j.get_property_value(solver_property1, db) for j in results.filter_by(instance=instance).all()]
            ys = [j.get_property_value(solver_property2, db) for j in results.filter_by(instance=instance).all()]
            points += zip(xs, ys, [instance] * len(xs))
    else:
        for instance in instances:
            res = results.filter_by(instance=instance, run=int(run)).first()
            points.append((
                res.get_property_value(solver_property1, db),
                res.get_property_value(solver_property2, db),
                instance
            ))

    return points


@plot.route('/<database>/experiment/<int:experiment_id>/scatter-plot-2properties/')
@require_phase(phases=(5, 6, 7))
@require_login
def scatter_1solver_result_vs_result_property(database, experiment_id):
    """ description """
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    solver_config = int(request.args['solver_config'])
    run = request.args['run']
    xscale = request.args['xscale']
    yscale = request.args['yscale']
    solver_property1 = request.args['solver_property1']
    solver_property2 = request.args['solver_property2']

    instances = [db.session.query(db.Instance).filter_by(idInstance=int(id)).first() for id in request.args.getlist('instances')]

    if solver_property1 != 'cputime':
        solver_prop1 = db.session.query(db.SolverProperty).get(int(solver_property1))

    if solver_property2 != 'cputime':
        solver_prop2 = db.session.query(db.SolverProperty).get(int(solver_property2))

    solver_config = db.session.query(db.SolverConfiguration).get(solver_config) or abort(404)

    points = scatter_1solver_result_vs_result_property_plot(db, exp, solver_config, instances, solver_property1, solver_property2, run)

    if solver_property1 == 'cputime':
        xlabel = 'CPU Time'
    else:
        xlabel = solver_prop1.name

    if solver_property2 == 'cputime':
        ylabel = 'CPU Time'
    else:
        ylabel = solver_prop2.name

    title = str(solver_config)

    max_x = max([p[0] for p in points] or [0]) * 1.1
    max_y = max([p[1] for p in points] or [0]) * 1.1

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Instance', xlabel, ylabel])
        for x, y, i in points:
            csv_writer.writerow([str(i), x, y])
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    elif request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.pdf'
        plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, format='pdf', xscale=xscale, yscale=yscale)
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename=str(solver_config) + '.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + '.png'
        pts = plots.scatter(points, xlabel, ylabel, title, max_x, max_y, filename, xscale=xscale, yscale=yscale)
        points = [(pts[i][0], pts[i][1], points[i][0], points[i][1], points[i][2]) for i in xrange(len(points))]
        if request.args.has_key('imagemap'):
            return render('/analysis/imagemap_result_vs_result.html', database=database, points=points, sc=solver_config)
        else:
            response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


@plot.route('/<database>/experiment/<int:experiment_id>/cactus-plot/')
@require_phase(phases=(5, 6, 7))
@require_login
def cactus_plot(database, experiment_id):
    """ Renders a cactus plot of the instances solved within a given "amount" of
        a result property of all solver configurations of the specified
        experiment
    """
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)


    results = db.session.query(db.ExperimentResult)
    results.enable_eagerloads(True).options(joinedload(db.ExperimentResult.solver_configuration))
    results.options(joinedload(db.ExperimentResult.solver_properties))
    results = results.filter_by(experiment=exp)
    instances = [int(id) for id in request.args.getlist('instances')]
    solver_property = request.args.get('solver_property') or 'cputime'
    if solver_property != 'cputime':
        solver_prop = db.session.query(db.SolverProperty).get(int(solver_property))

    solvers = []

    for sc in exp.solver_configurations:
        s = {'xs': [], 'ys': [], 'name': sc.get_name()}
        sc_res = results.filter_by(solver_configuration=sc, status=1).filter(db.ExperimentResult.resultCode.like('1%')).all()
        sc_res = sorted(sc_res, key=lambda r: r.get_property_value(solver_property, db))
        i = 1
        for r in sc_res:
            if r.Instances_idInstance in instances:
                s['ys'].append(r.get_property_value(solver_property, db))
                s['xs'].append(i)
                i += 1
        solvers.append(s)

    max_x = max([max(s['xs'] or [0]) for s in solvers]) + 10
    max_y = max([max(s['ys'] or [0]) for s in solvers]) * 1.1

    if solver_property == 'cputime':
        ylabel = 'CPU Time (s)'
        title = 'Number of solved instances within a given amount of CPU time'
    else:
        ylabel = solver_prop.name
        title = 'Number of solved instances within a given amount of ' + solver_prop.name

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        for s in solvers:
            csv_writer.writerow([s['name']])
            csv_writer.writerow(['number of solved instances'] + map(str, s['xs']))
            csv_writer.writerow(['CPU Time (s)'] + map(str, s['ys']))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    if request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'cactus.pdf'
        plots.cactus(solvers, max_x, max_y, ylabel, title, filename, format='pdf')
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename='instances_solved.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'cactus.png'
        plots.cactus(solvers, max_x, max_y, ylabel, title, filename)
        response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


@plot.route('/<database>/experiment/<int:experiment_id>/rtd-comparison-plot/')
@require_phase(phases=(5, 6, 7))
@require_login
def rtd_comparison_plot(database, experiment_id):
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    instance = db.session.query(db.Instance).filter_by(idInstance=int(request.args['instance'])).first() or abort(404)
    s1 = db.session.query(db.SolverConfiguration).get(int(request.args['solver_config1'])) or abort(404)
    s2 = db.session.query(db.SolverConfiguration).get(int(request.args['solver_config2'])) or abort(404)
    dim = int(request.args.get('dim', 700))

    results1 = [r.get_time() for r in db.session.query(db.ExperimentResult)
                                    .filter_by(experiment=exp,
                                               solver_configuration=s1,
                                               instance=instance).all()]
    results2 = [r.get_time() for r in db.session.query(db.ExperimentResult)
                                    .filter_by(experiment=exp,
                                               solver_configuration=s2,
                                               instance=instance).all()]

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Runtimes of the two solver configurations on ' + str(instance)])
        csv_writer.writerow([str(s1), str(s2)])
        for i in xrange(min(len(results1), len(results2))):
            csv_writer.writerow(map(str, [results1[i], results2[i]]))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    if request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'rtdcomp.png'
        plots.rtd_comparison(results1, results2, str(s1), str(s2), filename, format='pdf', dim=dim)
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename='rtdcomp.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'rtdcomp.png'
        plots.rtd_comparison(results1, results2, str(s1), str(s2), filename, 'png', dim=dim)
        response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


@plot.route('/<database>/experiment/<int:experiment_id>/rtds-plot/')
@require_phase(phases=(5, 6, 7))
@require_login
def rtds_plot(database, experiment_id):
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    instance = db.session.query(db.Instance).filter_by(idInstance=int(request.args['instance'])).first() or abort(404)
    solver_configs = [db.session.query(db.SolverConfiguration).get(int(id)) for id in request.args.getlist('sc')]

    results = []
    for sc in solver_configs:
        sc_results = db.session.query(db.ExperimentResult) \
                        .filter_by(experiment=exp, instance=instance,
                                   solver_configuration=sc).all()
        results.append((sc, [j.get_time() for j in sc_results]))

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Runtimes of the listed solver configurations on ' + str(instance)])
        for res in results:
            csv_writer.writerow([str(res[0])] + map(str, res[1]))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    if request.args.has_key('pdf'):
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'rtds.png'
        plots.rtds(results, filename, 'pdf')
        headers = Headers()
        headers.add('Content-Disposition', 'attachment', filename='rtds.pdf')
        response = Response(response=open(filename, 'rb').read(), mimetype='application/pdf', headers=headers)
        os.remove(filename)
        return response
    else:
        filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'rtds.png'
        plots.rtds(results, filename, 'png')
        response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
        os.remove(filename)
        return response


@plot.route('/<database>/experiment/<int:experiment_id>/rtd-plot/')
@require_phase(phases=(6, 7))
@require_login
def rtd(database, experiment_id):
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)
    sc = db.session.query(db.SolverConfiguration).get(int(request.args['solver_config'])) or abort(404)
    instance = db.session.query(db.Instance).filter_by(idInstance=int(request.args['instance'])).first() or abort(404)

    results = [r.get_time() for r in db.session.query(db.ExperimentResult)
                                    .filter_by(experiment=exp,
                                               solver_configuration=sc,
                                               instance=instance).all()]

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Runtimes of ' + str(sc) + ' on ' + str(instance)])
        csv_writer.writerow(map(str, results))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'rtd.png'
    plots.rtd(results, filename, 'png')
    response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
    os.remove(filename)
    return response


@plot.route('/<database>/experiment/<int:experiment_id>/kerneldensity-plot/')
@require_phase(phases=(6, 7))
@require_login
def kerneldensity(database, experiment_id):
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)
    sc = db.session.query(db.SolverConfiguration).get(int(request.args['solver_config'])) or abort(404)
    instance = db.session.query(db.Instance).filter_by(idInstance=int(request.args['instance'])).first() or abort(404)

    results = [r.get_time() for r in db.session.query(db.ExperimentResult)
                                    .filter_by(experiment=exp,
                                               solver_configuration=sc,
                                               instance=instance).all()]

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        csv_writer.writerow(['Runtimes of ' + str(sc) + ' on ' + str(instance)])
        csv_writer.writerow(map(str, results))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'kerneldens.png'
    plots.kerneldensity(results, filename, 'png')
    response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
    os.remove(filename)
    return response


@plot.route('/<database>/experiment/<int:experiment_id>/box-plots-plot/')
@require_phase(phases=(6, 7))
@require_login
def box_plots(database, experiment_id):
    db = models.get_database(database) or abort(404)
    exp = db.session.query(db.Experiment).get(experiment_id) or abort(404)

    instances = [db.session.query(db.Instance).filter_by(idInstance=int(id)).first() for id in request.args.getlist('instances')]
    solver_configs = [db.session.query(db.SolverConfiguration).get(int(id)) for id in request.args.getlist('solver_configs')]

    results = {}
    for sc in solver_configs:
        points = []
        for instance in instances:
            points += [res.get_time() for res in db.session.query(db.ExperimentResult).filter_by(experiment=exp, instance=instance, solver_configuration=sc).all()]
        results[str(sc)] = points

    if request.args.has_key('csv'):
        csv_response = StringIO.StringIO()
        csv_writer = csv.writer(csv_response)
        for k, v in results.iteritems():
            csv_writer.writerow([k] + map(str, v))
        csv_response.seek(0)

        headers = Headers()
        headers.add('Content-Type', 'text/csv')
        headers.add('Content-Disposition', 'attachment', filename="data.csv")
        return Response(response=csv_response.read(), headers=headers)

    filename = os.path.join(config.TEMP_DIR, g.unique_id) + 'boxplot.png'
    plots.box_plot(results, filename, 'png')
    response = Response(response=open(filename, 'rb').read(), mimetype='image/png')
    os.remove(filename)
    return response