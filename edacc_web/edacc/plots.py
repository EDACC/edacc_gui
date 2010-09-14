# -*- coding: utf-8 -*-
"""
    edacc.plots
    -----------

    Plotting functions using rpy2 to interface with the statistics language R.

    :copyright: (c) 2010 by Daniel Diepold.
    :license: MIT, see LICENSE for details.
"""

import numpy
from rpy2 import robjects
from rpy2.robjects.packages import importr
grdevices = importr('grDevices') # plotting target devices
np = importr('np') # non-parametric kernel smoothing methods
stats = importr('stats')

#cairo = importr('Cairo')
#cairo.CairoFonts(regular="Bitstream Vera Sans:style=Regular",
#                 bold="Bitstream Vera Sans:style=Bold",
#                 italic="Bitstream Vera Sans:style=Italic",
#                 symbol="Symbol")

def scatter(points, xlabel, ylabel, title, timeout, filename, format='png', scaling='none'):
    """ Scatter plot of the points given in the list :points:
        Each elemento of :points: should be a tuple (x, y).
        Returns a list with the points in device coordinates.
    """
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")

    # set margins to fit in labels on the right and top
    robjects.r.par(mar=robjects.FloatVector([4,4,6,6]))

    if scaling != 'log':
        # plot dashed line from (0,0) to (timeout,timeout)
        robjects.r.plot(robjects.FloatVector([0,timeout]),
                        robjects.FloatVector([0,timeout]),
                        type='l', col='black', lty=2,
                        xlim=robjects.r.c(0,timeout), ylim=robjects.r.c(0,timeout),
                        xaxs='i', yaxs='i',
                        xaxt='n', yaxt='n',
                        xlab='', ylab='')
        # to be able to plot in the same graph again
        robjects.r.par(new=1)

    xs = [p[0] for p in points]
    ys = [p[1] for p in points]

    robjects.r.options(scipen=10)

    min_x = 0
    min_y = 0

    if scaling == 'none':
        log = ''
    elif scaling == 'log':
        log = 'y'
        min_x = min([x for x in xs if x > 0])
        min_y = min([y for y in ys if y > 0])
    elif scaling == 'loglog':
        log = 'xy'
        min_x = min([x for x in xs if x > 0])
        min_y = min([y for y in ys if y > 0])

    min_v = min(min_x, min_y)

    # plot running times
    robjects.r.plot(robjects.FloatVector(xs), robjects.FloatVector(ys),
                    type='p', col='red', las = 1,
                    xlim=robjects.r.c(min_v,timeout), ylim=robjects.r.c(min_v,timeout),
                    xaxs='i', yaxs='i', log=log,
                    xlab='', ylab='', pch=3, tck=0.015,
                    **{'cex.axis': 1.2, 'cex.main': 1.5})

    # plot labels and axis
    robjects.r.axis(side=4, tck=0.015, las=1,
                    **{'cex.axis': 1.2, 'cex.main': 1.5}) # plot right axis
    robjects.r.axis(side=3, tck=0.015, las=1,
                    **{'cex.axis': 1.2, 'cex.main': 1.5}) # plot top axis
    robjects.r.mtext(ylabel, side=4, line=3, cex=1.2) # right axis label
    robjects.r.mtext(xlabel, side=3, padj=0, line=3, cex=1.2) # top axis label
    robjects.r.mtext(title, padj=-1.7, side=3, line=3, cex=1.7) # plot title

    pts = zip(robjects.r.grconvertX(robjects.FloatVector(xs), "user", "device"),
              robjects.r.grconvertY(robjects.FloatVector(ys), "user", "device"))
    grdevices.dev_off()
    return pts

def cactus(solvers, max_x, max_y, filename, format='png'):
    """ Cactus plot of the passed solvers configurations. `solvers` has to be
        a list of dictionaries with the keys `xs`, `ys` and `name`. For each
        y in `ys` the corresponding x in `xs` should be the number of
        instances solved within y seconds.
    """
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")

    # list of colors used in the defined order for the different solvers
    colors = [
        'red', 'green', 'blue', 'darkgoldenrod1', 'darkolivegreen',
        'darkorchid', 'deeppink', 'darkgreen', 'blue4'
    ]

    # plot without data to create the frame
    robjects.r.plot(robjects.FloatVector([]), robjects.FloatVector([]),
                    type='p', col='red', las = 1,
                    xlim=robjects.r.c(0,max_x), ylim=robjects.r.c(0,max_y),
                    xaxs='i', yaxs='i',
                    xlab='',ylab='', **{'cex.main': 1.5})
    robjects.r.par(new=1)

    point_style = 0
    for s in solvers:
        xs = s['xs']
        ys = s['ys']

        # plot points
        robjects.r.plot(robjects.FloatVector(xs), robjects.FloatVector(ys),
                        type='p', col=colors[point_style], pch=point_style,
                        xlim=robjects.r.c(0,max_x), ylim=robjects.r.c(0,max_y),
                        xaxs='i', yaxs='i',
                        axes=False, xlab='',ylab='', **{'cex.main': 1.5})
        robjects.r.par(new=1)

        # plot lines
        robjects.r.plot(robjects.FloatVector(xs), robjects.FloatVector(ys),
                        type='l', col=colors[point_style],lty=1,
                        xlim=robjects.r.c(0,max_x), ylim=robjects.r.c(0,max_y),
                        xaxs='i', yaxs='i',
                        axes=False, xlab='',ylab='', **{'cex.main': 1.5})
        robjects.r.par(new=1)

        point_style += 1

    # plot labels and axes
    robjects.r.mtext('number of solved instances', side=1,
                     line=3, cex=1.2) # bottom axis label
    robjects.r.mtext('CPU Time (s)', side=2, padj=0,
                     line=3, cex=1.2) # left axis label
    robjects.r.mtext('Number of instances solved within a given amount of time',
                     padj=1, side=3, line=3, cex=1.7) # plot title

    # plot legend
    robjects.r.legend(1, max_y - (max_y * 0.03),
                      legend=robjects.StrVector([s['name'] for s in solvers]),
                      col=robjects.StrVector(colors[:len(solvers)]),
                      pch=robjects.IntVector(range(len(solvers))), lty=1)

    grdevices.dev_off()


def rtd_comparison(results1, results2, solver1, solver2, filename, format='png'):
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")

    max_x = max([max(results1), max(results2)])

    #print stats.wilcox_test(robjects.FloatVector(results1), robjects.FloatVector(results2), paired=False)

    # plot without data to create the frame
    robjects.r.plot(robjects.FloatVector([]), robjects.FloatVector([]),
                    type='p', col='red', las = 1,
                    xlim=robjects.r.c(0.0, max_x), ylim=robjects.r.c(0.0, 1.0),
                    xaxs='i', yaxs='i',
                    xlab='',ylab='', **{'cex.main': 1.5})
    robjects.r.par(new=1)

    # plot the two distributions
    robjects.r.plot(robjects.r.ecdf(robjects.FloatVector(results1)),
                    main='',
                    xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='red',
                    xlim=robjects.r.c(0.0,max_x), ylim=robjects.r.c(0.0,1.0))
    robjects.r.par(new=1)
    robjects.r.plot(robjects.r.ecdf(robjects.FloatVector(results2)),
                    main='',
                    xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='blue',
                    xlim=robjects.r.c(0.0,max_x), ylim=robjects.r.c(0.0,1.0))

    # plot labels and axes
    robjects.r.mtext('CPU Time (s)', side=1,
                     line=3, cex=1.2) # bottom axis label
    robjects.r.mtext('P(solve within x seconds)', side=2, padj=0,
                     line=3, cex=1.2) # left axis label
    robjects.r.mtext('RTD Comparison',
                     padj=1, side=3, line=3, cex=1.7) # plot title

    # plot legend
    robjects.r.legend(max_x - (max_x * 0.3), 0.1,
                      legend=robjects.StrVector([solver1, solver2]),
                      col=robjects.StrVector(['red', 'blue']),
                      pch=robjects.IntVector([0,1]), lty=1)

    grdevices.dev_off()


def box_plot(data, filename, format='png'):
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")

    for key in data:
        data[key] = robjects.FloatVector(data[key])

    robjects.r.boxplot(robjects.DataFrame(data), main="Boxplot", horizontal=True)

    grdevices.dev_off()


def hist(data, filename, format='png'):
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")


    #robjects.r.hist(robjects.FloatVector(data), main="Histogram", breaks=30,
    #                xlab='CPU Time', probability=True)
    d = np.npudens(robjects.FloatVector(data))
    robjects.r.plot(d, main='Non-parametric kernel density estimation',
                    xlab='CPU Time', ylab='P(solve)')

    grdevices.dev_off()

def ecdf(data, filename, format='png'):
    if format == 'png':
        #cairo.CairoPNG(file=filename, units="px", width=600,
        #               height=600, bg="white", pointsize=14)
        grdevices.png(file=filename, units="px", width=600,
                      height=600, type="cairo")
    elif format == 'pdf':
        grdevices.bitmap(file=filename, type="pdfwrite")

    robjects.r.plot(robjects.r.ecdf(robjects.FloatVector(data)),
                    main="Empirical Cumulative Distribution Function",
                    xlab='CPU time', ylab='P(solve)',
                    xlim=robjects.r.c(0,max(data)), ylim=robjects.r.c(0,1.0))
    #robjects.r.par(new=1)
    #exp = robjects.r.pexp(robjects.FloatVector(range(int(max(data)))), rate=1.0/numpy.average(data))
    #robjects.r.plot(exp, main='', xlab='', ylab='',
    #                xlim=robjects.r.c(0,max(data)), ylim=robjects.r.c(0,1.0))

    grdevices.dev_off()