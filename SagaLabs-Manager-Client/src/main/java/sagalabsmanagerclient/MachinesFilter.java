package sagalabsmanagerclient;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import sagalabsmanagerclient.controllers.MachinesController;

public class MachinesFilter {
    private boolean osSET = false;
    private boolean stateSET = false;
    private boolean nameSET = false;
    private boolean resourceGroupSET = true;

    private Predicate<MachinesVM> resourceGroupPredicate = null;
    private Predicate<MachinesVM> osPredicate = null;
    private Predicate<MachinesVM> statePredicate = null;
    private Predicate<MachinesVM> namePredicate = null;

    public MachinesFilter(String osFilter, String stateFilter, String nameFilter, MachinesTab currentTab) {
        String finalNameFilter = nameFilter.replaceAll("\\s", "");
        if(!nameFilter.equals("")) {
            nameSET = true;
            namePredicate = e -> e.getVmName().contains(finalNameFilter);
        }
        String finalStateFilter = stateFilter.replaceAll("\\s", "");
        if(!stateFilter.equals("")) {
            stateSET = true;
            statePredicate = e -> e.getState().equalsIgnoreCase(finalStateFilter);
        }
        String finalOsFilter = osFilter.replaceAll("\\s", "");
        if(!finalOsFilter.equals("")) {
            osSET = true;
            osPredicate = e -> e.getOs().equalsIgnoreCase(finalOsFilter);
        }
        if(Objects.equals(currentTab.getTab().getText(), "All")) {
            resourceGroupSET = false;
        }
        else {
            resourceGroupPredicate = e -> e.getResourceGroup().equals(currentTab.getResourceGroup());
        }
    }

    public void setPredicates(MachinesTab tab) {
        // Sets predicates for each combination of possibilities
        if(resourceGroupSET && nameSET && stateSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate.and(osPredicate).and(statePredicate).and(resourceGroupPredicate));
        } else if (resourceGroupSET && nameSET && stateSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(namePredicate).and(statePredicate));
        } else if (resourceGroupSET && nameSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(namePredicate).and(osPredicate));
        } else if (resourceGroupSET && nameSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(namePredicate));
        } else if (resourceGroupSET && stateSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(statePredicate).and(osPredicate));
        } else if (resourceGroupSET && stateSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(statePredicate));
        } else if (resourceGroupSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate.and(osPredicate));
        } else if (resourceGroupSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(resourceGroupPredicate);
        } else if (nameSET && stateSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate.and(statePredicate).and(osPredicate));
        } else if (nameSET && stateSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate.and(statePredicate));
        } else if (nameSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate.and(osPredicate));
        } else if (nameSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate);
        } else if (stateSET && osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate.and(osPredicate));
        } else if (stateSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(namePredicate);
        } else if (osSET) {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(osPredicate);
        } else {
            ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(null);
        }
    }
}
