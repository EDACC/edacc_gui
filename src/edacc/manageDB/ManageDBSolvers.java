/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCApp;
import edacc.model.SolverIsInExperimentException;
import edacc.EDACCManageDBMode;
import edacc.EDACCSolverBinaryDlg;
import edacc.model.CostBinary;
import edacc.model.CostDAO;
import edacc.model.DatabaseConnector;
import edacc.model.NoConnectionToDBException;
import edacc.model.Parameter;
import edacc.model.ParameterGraphDAO;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverBinariesDAO;
import edacc.model.SolverDAO;
import edacc.model.SolverNotInDBException;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import edacc.parameterspace.graph.ParameterGraph;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

/**
 *
 * @author dgall
 */
public class ManageDBSolvers implements Observer {

    private EDACCManageDBMode gui;
    private SolverTableModel solverTableModel;
    private Solver currentSolver;
    private ManageDBParameters manageDBParameters;
    private SolverBinariesTableModel solverBinariesTableModel;

    public ManageDBSolvers(EDACCManageDBMode gui, SolverTableModel solverTableModel, ManageDBParameters manageDBParameters, SolverBinariesTableModel solverBinariesTableModel) {
        this.gui = gui;
        this.solverTableModel = solverTableModel;
        this.manageDBParameters = manageDBParameters;
        this.solverBinariesTableModel = solverBinariesTableModel;
        DatabaseConnector.getInstance().addObserver(this);
    }

    public Solver getCurrentSolver() {
        return currentSolver;
    }

    /**
     * Loads all solvers from the DB and adds it to the Solver table.
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public void loadSolvers() throws NoConnectionToDBException, SQLException {
        solverTableModel.clear();
        SolverDAO.clearCache();
        for (Solver s : SolverDAO.getAll()) {
            solverTableModel.addSolver(s);
        }
    }

    /**
     * Applies the name and the description of a solver.
     * @param name
     * @param description
     * @return <code>true</code> if the solver changed
     */
    public boolean applySolver(String name, String description, String author, String version) {
        if (currentSolver != null) {
            if (currentSolver.getName() != null && currentSolver.getName().equals(name)
                    && currentSolver.getDescription().equals(description)
                    && currentSolver.getAuthors().equals(author)
                    && currentSolver.getVersion().equals(version)) {
                return false;
            }
            currentSolver.setName(name);
            currentSolver.setDescription(description);
            currentSolver.setAuthor(author);
            currentSolver.setVersion(version);
            return true;
        }
        return false;
    }

    public void saveSolvers() {
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                try {
                    saveSolvers(task);
                } catch (final SQLException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    "Solvers cannot be saved. There is a problem with the Database: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (final FileNotFoundException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    "Solvers cannot be saved because a file couldn't be found: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (final NoSolverBinarySpecifiedException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (final NoSolverNameSpecifiedException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (final IOException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    "IO exception while reading solver data from the filesystem" + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (final NoSuchAlgorithmException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (final JAXBException ex) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            EDACCApp.getLogger().logException(ex);
                            JOptionPane.showMessageDialog(gui,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
        }, true);
    }

    /**
     * Tries to save all solvers in the solver table to DB.
     * If a solver is already saved in the DB, it will update its data in the DB.
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void saveSolvers(final Tasks task) throws SQLException, FileNotFoundException, NoSolverBinarySpecifiedException, NoSolverNameSpecifiedException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, JAXBException {
        task.setOperationName("Saving solvers...");
        int countSolvers = solverTableModel.getSolvers().size();
        for (int i = 0; i < countSolvers; i++) {
            Solver s = solverTableModel.getSolver(i);
            task.setTaskProgress((float) i / (float) countSolvers);
            task.setStatus("Saving solver " + s.getName() + " (" + i + " of " + countSolvers + ")");
            Vector<Parameter> params = manageDBParameters.getParametersOfSolver(s);
            SolverDAO.save(s);
            manageDBParameters.rehash(s, params);
        }
        // save parameters
        for (Solver s : solverTableModel.getSolvers()) {
            manageDBParameters.saveParameters(s);
        }
    }

    public void newSolver() {
        Solver s = new Solver();
        solverTableModel.addSolver(s);
        solverTableModel.fireTableDataChanged();
        manageDBParameters.addDefaultParameters(s);
    }

    /**
     * Shows the sovler with the specified index, which means: All
     * buttons for the solver are activated and its details are shown.
     * If the index is invalid, no solver will be shown and the solver
     * specific buttons are deactivated.
     * @param index
     */
    public void showSolver(int index) {
        currentSolver = solverTableModel.getSolver(index); // will be null if no solver selected!
        gui.showSolverDetails(currentSolver);
        gui.showSolverBinariesDetails(currentSolver == null ? null : currentSolver.getSolverBinaries());
        gui.showCostBinaryDetails(currentSolver == null ? null : currentSolver.getCostBinaries());
    }

    public void addSolverBinary(File[] binary) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, SQLException, SolverAlreadyInDBException {
        if (binary.length == 0) {
            return;
        }
        Arrays.sort(binary);
        SolverBinaries b = new SolverBinaries(currentSolver);
        b.setBinaryArchive(binary);
        b.setBinaryName(binary[0].getName());
        try {
            FileInputStreamList is = new FileInputStreamList(binary);
            SequenceInputStream seq = new SequenceInputStream(is);
            String md5 = Util.calculateMD5(seq);
            if (hasDuplicates(md5)) {
                if (JOptionPane.showConfirmDialog(gui,
                        "There already exists a solver binary with the same "
                        + "checksum. Do you want to add this binary anyway?",
                        "Duplicate solver binary",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                        == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            b.setMd5(md5);
            Util.removeCommonPrefix(b);
            new EDACCSolverBinaryDlg(EDACCApp.getApplication().getMainFrame(), b, this, EDACCSolverBinaryDlg.DialogMode.CREATE_MODE).setVisible(true);
            gui.showSolverBinariesDetails(currentSolver.getSolverBinaries());
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(gui, "You have to choose some files!", "No files chosen!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasDuplicates(String md5) {
        for (SolverBinaries b : currentSolver.getSolverBinaries()) {
            if (md5.equals(b.getMd5())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Edits a solver binary with all details and changes also the file archive.
     * @param binary
     * @param solverBin
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public void editSolverBinary(File[] binary, SolverBinaries solverBin) throws IOException, NoSuchAlgorithmException {
        if (binary.length == 0) {
            return;
        }
        Arrays.sort(binary);
        solverBin.setBinaryArchive(binary);
        FileInputStreamList is = new FileInputStreamList(binary);
        SequenceInputStream seq = new SequenceInputStream(is);
        String md5 = Util.calculateMD5(seq);
        if (hasDuplicates(md5)) {
            if (JOptionPane.showConfirmDialog(gui,
                    "There already exists a solver binary with the same "
                    + "checksum. Do you want to add this binary anyway?",
                    "Duplicate solver binary",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                    == JOptionPane.NO_OPTION) {
                return;
            }
        }
        solverBin.setMd5(md5);
        Util.removeCommonPrefix(solverBin);
        new EDACCSolverBinaryDlg(EDACCApp.getApplication().getMainFrame(), solverBin, this, EDACCSolverBinaryDlg.DialogMode.EDIT_MODE).setVisible(true);
        gui.showSolverBinariesDetails(currentSolver.getSolverBinaries());
    }

    /**
     * Edits a solver binary but doesn't change the archive. Only the details
     * like name, run command or run path are changed.
     * @param solverBin
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public void editSolverBinaryDetails(SolverBinaries solverBin) throws IOException, NoSuchAlgorithmException, SQLException {
        // create file list of binary
        setFileArrayOfSolverBinary(solverBin);
        new EDACCSolverBinaryDlg(EDACCApp.getApplication().getMainFrame(), solverBin, this, EDACCSolverBinaryDlg.DialogMode.EDIT_MODE).setVisible(true);
        // reset binary archive
        solverBin.setBinaryArchive(null);
        // refresh gui
        gui.showSolverBinariesDetails(currentSolver.getSolverBinaries());
    }

    private void setFileArrayOfSolverBinary(SolverBinaries solverBin) throws SQLException, IOException {
        // make temporary directory
        File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "edacctmpdir");
        tmpDir.mkdirs();
        ZipInputStream zis = new ZipInputStream(SolverBinariesDAO.getZippedBinaryFile(solverBin));
        LinkedList<File> bins = new LinkedList<File>(); // the binary files in the zip 
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            bins.add(new File(tmpDir.getAbsolutePath() + System.getProperty("file.separator") + entry.getName()));
        }
        solverBin.setBinaryArchive(bins.toArray(new File[bins.size()]));
        solverBin.setRootDir(tmpDir.getAbsolutePath());
    }

    public void addSolverBinary(SolverBinaries solverBin) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException {
        currentSolver.addSolverBinary(solverBin);
    }

    public void addSolverCode(File[] code) throws FileNotFoundException {
        for (File c : code) {
            if (!c.exists()) {
                throw new FileNotFoundException("Couldn't find file \"" + c.getName() + "\".");
            }
        }
        currentSolver.setCodeFile(code);
    }

    /**
     * Removes the current solver from the solver table model.
     * If it is persisted in the db, it will also remove it from the db.
     * @throws SolverIsInExperimentException if the solver is used in an experiment.
     * @throws SQLException if an SQL error occurs while deleting the solver.
     */
    public void removeSolver() throws SolverIsInExperimentException, SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException {
        removeSolver(currentSolver);

        solverTableModel.removeSolver(currentSolver);
    }

    /**
     * Removes the specified solver from the solver table model.
     * If it is persisted in the db, it will also remove it from the db.
     * @throws SolverIsInExperimentException if the solver is used in an experiment.
     * @throws SQLException if an SQL error occurs while deleting the solver.
     */
    public void removeSolver(Solver s) throws SolverIsInExperimentException, SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException {
        try {
            SolverDAO.removeSolver(s);
        } catch (SolverNotInDBException ex) {
            // if the solver isn't in the db, just remove it from the table model
        }
        solverTableModel.removeSolver(s);
    }

    public void removeSolverBinary(SolverBinaries b) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException, NoSuchAlgorithmException {
        Solver s = SolverDAO.getById(b.getIdSolver());
        if (s.getSolverBinaries().size() <= 1) {
            throw new NoSolverBinarySpecifiedException("There must be at least one binary remaining for solver " + s.getName() + "!");
        }
        b.setDeleted();
        SolverBinariesDAO.save(b);
        solverBinariesTableModel.setSolverBinaries(currentSolver.getSolverBinaries());
    }

    /**
     * Exports the binary of a solver to the file system.
     * @param s The solver to be exported
     * @param f The location where the binary shall be stored. If it is a directory,
     * the solverName field of the solver will be used as filename.
     */
    public void exportSolver(final Solver[] s, final File f) {
        Tasks.startTask(new TaskRunnable() {

            @Override
            public void run(Tasks task) {
                try {
                    startExportSolverTask(s, f, task);
                } catch (final Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(gui, "An error occured while exporting solver binaries: \n" + e.getMessage(), "Error while exporting solver binaries", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
        }, true);
    }
    int entryCounter = 0;

    /**
     * Exports all solver binaries, cost binaries and code of the given solvers
     * and shows a progress bar.
     * @param solvers
     * @param f
     * @param task
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void startExportSolverTask(Solver[] solvers, File f, Tasks task) throws FileNotFoundException, SQLException, IOException, JAXBException {
        task.setOperationName("Exporting solver");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        FileOutputStream fos;
        if (f.isDirectory()) {
            fos = new FileOutputStream(f.getAbsolutePath() + System.getProperty("file.separator") + "exported_solvers-" + dateFormat.format(Calendar.getInstance().getTime()) + ".zip");
        } else {
            fos = new FileOutputStream(f);
        }
        ZipOutputStream zos = new ZipOutputStream(fos);

        // calculate number of zip entries to be written
        int numEntries = 0;
        for (Solver s : solvers) {
            numEntries += s.getSolverBinaries().size();
            numEntries += s.getCostBinaries().size();
        }
        numEntries += solvers.length; // count code of solvers

        entryCounter = 0;
        int solverCounter = 1;
        for (Solver s : solvers) {
            task.setStatus("Exporting solver binaries of solver " + solverCounter + "/" + solvers.length);
            exportSolverBinaries(s, task, zos, numEntries);
            task.setStatus("Exporting cost binaries of solver" + solverCounter + "/" + solvers.length);
            exportSolverCosts(s, task, zos, numEntries);
            task.setStatus("Exporting code of solver" + solverCounter + "/" + solvers.length);
            exportSolverCode(s, task, zos, numEntries);
            task.setStatus("Exporting parameters of solver" + solverCounter + "/" + solvers.length);
            exportSolverReadMe(s, task, zos, numEntries);
            task.setStatus("Exporting parameter graph of solver" + solverCounter + "/" + solvers.length);
            exportSolverParameterGraph(s, zos, numEntries);
            solverCounter++;
        }
        zos.close();
        fos.close();
    }

    private void exportSolverCosts(Solver s, Tasks task, ZipOutputStream zos, int numBins) throws SQLException, IOException {
        List<CostBinary> costs = s.getCostBinaries();
        HashMap<String, String> names = new HashMap<String, String>();
        for (int i = 0; i < costs.size(); i++) {
            task.setTaskProgress((float) ++entryCounter / (float) numBins);
            CostBinary b = costs.get(i);
            InputStream costStream = CostDAO.getZippedBinaryFile(b);
            ZipInputStream zis = new ZipInputStream(costStream);
            ZipEntry entry;
            // calculate name for the following entries
            String name;
            if (b.getVersion().equals("")) {
                name = b.getBinaryName();
            } else {
                name = b.getBinaryName() + "_" + b.getVersion();
            }
            // check for duplicate names
            int j = 1;
            while (names.containsKey(name)) {
                if (b.getVersion().equals("")) {
                    name = name = b.getBinaryName() + "-" + j++;
                } else {
                    name = b.getBinaryName() + "_" + b.getVersion() + "-" + j++;
                }
            }
            names.put(name, name);
            // write cost entries
            while ((entry = zis.getNextEntry()) != null) {
                ZipEntry newEntry = new ZipEntry(s.getName() + "/costs/" + name + "/" + entry.getName());
                zos.putNextEntry(newEntry);

                for (int c = zis.read(); c != -1; c = zis.read()) {
                    zos.write(c);
                }
                zos.closeEntry();
                zis.closeEntry();
            }
            zis.close();
            costStream.close();
        }
    }

    private void exportSolverBinaries(Solver s, Tasks task, ZipOutputStream zos, int numBins) throws SQLException, IOException {
        Vector<SolverBinaries> bins = s.getSolverBinaries();
        HashMap<String, String> names = new HashMap<String, String>();
        for (int i = 0; i < bins.size(); i++) {
            task.setTaskProgress((float) ++entryCounter / (float) numBins);
            SolverBinaries b = bins.get(i);
            InputStream binStream = SolverBinariesDAO.getZippedBinaryFile(b);
            ZipInputStream zis = new ZipInputStream(binStream);
            ZipEntry entry;
            // calculate name for the following entries
            String name;
            if (b.getVersion().equals("")) {
                name = b.getBinaryName();
            } else {
                name = b.getBinaryName() + "_" + b.getVersion();
            }
            // check for duplicate names
            int j = 1;
            while (names.containsKey(name)) {
                if (b.getVersion().equals("")) {
                    name = name = b.getBinaryName() + "-" + j++;
                } else {
                    name = b.getBinaryName() + "_" + b.getVersion() + "-" + j++;
                }
            }
            names.put(name, name);
            // write binary entries
            while ((entry = zis.getNextEntry()) != null) {
                ZipEntry newEntry = new ZipEntry(s.getName() + "/bin/" + name + "/" + entry.getName());
                zos.putNextEntry(newEntry);
                for (int c = zis.read(); c != -1; c = zis.read()) {
                    zos.write(c);
                }
                zos.closeEntry();
                zis.closeEntry();
            }
            zis.close();
            binStream.close();
        }
    }

    private void exportSolverCode(Solver s, Tasks task, ZipOutputStream zos, int numBins) throws SQLException, IOException {
        task.setTaskProgress((float) ++entryCounter / (float) numBins);
        InputStream codeStream = SolverDAO.getZippedCodeFile(s);
        if (codeStream == null) {
            return; // no code for this solver
        }
        ZipInputStream zis = new ZipInputStream(codeStream);
        ZipEntry entry;

        // write code entries
        while ((entry = zis.getNextEntry()) != null) {
            ZipEntry newEntry = new ZipEntry(s.getName() + "/src/" + entry.getName());
            zos.putNextEntry(newEntry);
            for (int c = zis.read(); c != -1; c = zis.read()) {
                zos.write(c);
            }
            zos.closeEntry();
            zis.closeEntry();
        }
        zis.close();
        codeStream.close();
    }

    /**
     * Exports a ReadMe-file which contains the start commands of each binary and
     * all the solver parameters.
     * @param s
     * @param task
     * @param zos
     * @param numBins
     * @throws SQLException
     * @throws IOException
     */
    private void exportSolverReadMe(Solver s, Tasks task, ZipOutputStream zos, int numBins) throws SQLException, IOException {
        Vector<Parameter> params = manageDBParameters.getParametersOfSolver(s);
        Vector<SolverBinaries> bins = s.getSolverBinaries();

        ZipEntry newEntry = new ZipEntry(s.getName() + "/ReadMe.txt");
        zos.putNextEntry(newEntry);
        PrintWriter pout = new PrintWriter(zos);
        pout.println("This is the ReadMe-File of the solver " + s.getName());
        pout.println("It has been created automatically by EDACC.");
        pout.println();

        pout.println("1. Solver Binaries");
        pout.println("==================");
        pout.println();
        pout.println("The solver has been compiled to the following binary "
                + "versions which can be found in the subdirectory \"bin\".");
        pout.println("If the binary needs a specific run command (eg. java -jar), "
                + "then this is mentioned in the following list.");
        pout.println("The start commands are relative to the path of the binary, eg. bin/myBinary/.");
        pout.println();
        for (SolverBinaries b : bins) {
            pout.println("* " + b.getBinaryName() + ": ");
            String runCommand = "";
            if (b.getRunCommand() != null)
                runCommand = b.getRunCommand();
            pout.println("\tStart command: " + runCommand + " ." + b.getRunPath());
        }
        pout.println();
        pout.println("2. Parameters");
        pout.println("=============");
        pout.println();
        pout.println("The solver binaries can be executed with the following commands: ");
        pout.println();
        pout.println("Order\tName\tPrefix\tboolean\tdefault value\tmandatory\tspace\t");
        Collections.sort(params, new ParameterOrderComparator()); // sort params list by order
        for (Parameter p : params) {
            pout.println(p.getOrder() + "\t" + p.getName() + "\t" + p.getPrefix() + "\t"
                    + (p.getHasValue() ? "X" : "") + "\t" + p.getDefaultValue()
                    + "\t" + (p.isMandatory() ? "X" : "") + "\t" + (p.getSpace() ? "X" : ""));
        }
        pout.flush();
        zos.closeEntry();
    }

    private void exportSolverParameterGraph(Solver s, ZipOutputStream zos, int numBins) throws SQLException, IOException, JAXBException {
        ZipEntry newEntry = new ZipEntry(s.getName() + "/paramgraph-" + s.getName() + ".graph");
        zos.putNextEntry(newEntry);

        ParameterGraph graph = ParameterGraphDAO.loadParameterGraph(s);
        if (graph == null)
            return;
        InputStream stream = ParameterGraphDAO.getParameterGraphXMLStream(graph);
        byte[] buf = new byte[2048];
        int len;
        while ((len = stream.read(buf)) > 0) {
            zos.write(buf, 0, len);
        }
        zos.closeEntry();
        stream.close();
    }


    @Override
    public void update(Observable o, Object arg) {
        solverTableModel.clear();
    }

    public void removeSolverBinaries(Solver s) throws SQLException {
        SolverBinariesDAO.removeBinariesOfSolver(s);
    }

    void selectSolverBinary(boolean selected) {
        gui.enableSolverBinaryButtons(selected);
    }

    private class ParameterOrderComparator implements Comparator<Parameter> {

        @Override
        public int compare(Parameter p1, Parameter p2) {
            return ((Integer) p1.getOrder()).compareTo(p2.getOrder());
        }
        
    }
}
