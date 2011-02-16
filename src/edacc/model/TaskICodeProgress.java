/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.EDACCTaskView;
import javax.swing.SwingUtilities;

/**
 *
 * @author simon
 */
public class TaskICodeProgress implements SevenZip.ICodeProgress {

    private Long last = 0l;
    private long time;
    private String eta = "";
    private long size;
    private Integer id;
    private EDACCTaskView view;

    public TaskICodeProgress(long size, String name) {
        this.size = size;
        this.view = Tasks.getTaskView();
        if (view != null) {
            id = view.getSubTaskId();
            view.setMessage(id, name);
        }
    }

    @Override
    public void SetProgress(final long done, long done_compr) {
        /*  if (System.currentTimeMillis() - time > 1000) {
        long leta = (size - done) * 1000 / (done - last) / (System.currentTimeMillis() - time);
        eta = leta + " sec";
        last = done;
        time = System.currentTimeMillis();
        System.out.println(done + " of " + size + " " + done / (float) size * 100 + "% " + eta);

        }*/
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (view != null && id != null) {
                    view.setProgress(id, done / (float) size * 100);
                }
            }
        });


    }

    public void finished() {
       if (view != null) {
           view.subTaskFinished(id);
       }
    }
}
