package org.palladiosimulator.simulizar.ui.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.simulizar.launcher.SimulizarConstants;

import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;
import de.uka.ipd.sdq.workflow.pcm.runconfig.ProtocomFileNamesInputTab;

/**
 * File name input tab for SimuLizar. Uses middleware and eventmiddle ware input fields for Monitor
 * Repository models and SDM models.
 */
public class InterpreterFileNamesInputTab extends ProtocomFileNamesInputTab {

    // input fields
    /** Text field for path to Monitor Repository file. */
    protected Text monitorRepositoryFile;
    /** Text field for path to reconfiguration rules folder. */
    protected Text reconfigurationRulesFolder;
    /** Text field for path to usage evolution file. */
    protected Text usageEvolutionFile;

    /**
     * @see de.uka.ipd.sdq.workflow.launchconfig.tabs.FileNamesInputTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        /**
         * Create Monitor Repository file section
         */
        monitorRepositoryFile = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Optional: Monitor Repository File",
                SimulizarConstants.MONITORING_SPECIFICATION_FILE_EXTENSION, monitorRepositoryFile,
                "Select Monitor Repository File", getShell(), SimulizarConstants.DEFAULT_MONITORE_REPOSITORY_FILE);

        /**
         * Create reconfiguration rules folder section
         */
        reconfigurationRulesFolder = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFolderInputSection(container, modifyListener, "Optional: Reconfiguration rules folder ",
                reconfigurationRulesFolder, "Select Reconfiguration Rules Folder", getShell(),
                SimulizarConstants.DEFAULT_RECONFIGURATION_RULES_FOLDER);

        /**
         * Create UsageEvolution file section
         */
        usageEvolutionFile = new Text(container, SWT.SINGLE | SWT.BORDER);
        TabHelper.createFileInputSection(container, modifyListener, "Optional: Usage Evolution File",
                SimulizarConstants.USAGEEVOLUTION_FILE_EXTENSION, usageEvolutionFile, "Select Usage Evolution File",
                getShell(), SimulizarConstants.DEFAULT_USAGEEVOLUTION_FILE);

    }

    /**
     * @see de.uka.ipd.sdq.workflow.launchconfig.tabs.FileNamesInputTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
        super.initializeFrom(configuration);
        try {
            monitorRepositoryFile.setText(configuration.getAttribute(SimulizarConstants.MONITOR_REPOSITORY_FILE,
                    SimulizarConstants.DEFAULT_MONITORE_REPOSITORY_FILE));
        } catch (final CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Monitor Repository File", e.getMessage());
        }

        try {
            reconfigurationRulesFolder.setText(configuration.getAttribute(
                    SimulizarConstants.RECONFIGURATION_RULES_FOLDER,
                    SimulizarConstants.DEFAULT_RECONFIGURATION_RULES_FOLDER));
        } catch (final CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Reconfiguration Rules Folder", e.getMessage());
        }

        try {
            usageEvolutionFile.setText(configuration.getAttribute(SimulizarConstants.USAGEEVOLUTION_FILE,
                    SimulizarConstants.DEFAULT_USAGEEVOLUTION_FILE));
        } catch (final CoreException e) {
            LaunchConfigPlugin.errorLogger(getName(), "Usage Evolution File", e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.
     * ILaunchConfigurationWorkingCopy)
     */
    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        configuration.setAttribute(SimulizarConstants.MONITOR_REPOSITORY_FILE, monitorRepositoryFile.getText());
        configuration.setAttribute(SimulizarConstants.RECONFIGURATION_RULES_FOLDER,
                reconfigurationRulesFolder.getText());
        configuration.setAttribute(SimulizarConstants.USAGEEVOLUTION_FILE, usageEvolutionFile.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.
     * ILaunchConfigurationWorkingCopy)
     */
    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
        configuration.setAttribute(SimulizarConstants.MONITOR_REPOSITORY_FILE,
                SimulizarConstants.DEFAULT_MONITORE_REPOSITORY_FILE);
        configuration.setAttribute(SimulizarConstants.RECONFIGURATION_RULES_FOLDER,
                SimulizarConstants.DEFAULT_RECONFIGURATION_RULES_FOLDER);
        configuration.setAttribute(SimulizarConstants.USAGEEVOLUTION_FILE,
                SimulizarConstants.DEFAULT_USAGEEVOLUTION_FILE);
    }

    /**
     * @see de.uka.ipd.sdq.workflow.launchconfig.tabs.FileNamesInputTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public boolean isValid(final ILaunchConfiguration launchConfig) {
        return true;
    }

}
