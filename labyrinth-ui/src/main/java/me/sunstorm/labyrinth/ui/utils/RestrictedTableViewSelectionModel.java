package me.sunstorm.labyrinth.ui.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

public class RestrictedTableViewSelectionModel<S> extends TableView.TableViewSelectionModel<S> {


    /**
     * Builds a default TableViewSelectionModel instance with the provided
     * TableView.
     *
     * @param tableView The TableView upon which this selection model should
     *                  operate.
     * @throws NullPointerException TableView can not be null.
     */
    public RestrictedTableViewSelectionModel(TableView<S> tableView) {
        super(tableView);
    }

    @Override
    public ObservableList<TablePosition> getSelectedCells() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public boolean isSelected(int row, TableColumn<S, ?> column) {
        return false;
    }

    @Override
    public void select(int row, TableColumn<S, ?> column) {

    }

    @Override
    public void clearAndSelect(int row, TableColumn<S, ?> column) {

    }

    @Override
    public void clearSelection(int row, TableColumn<S, ?> column) {

    }

    @Override
    public void selectLeftCell() {

    }

    @Override
    public void selectRightCell() {

    }

    @Override
    public void selectAboveCell() {

    }

    @Override
    public void selectBelowCell() {

    }
}
