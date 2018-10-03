package kz.topsecurity.client.service.trackingService.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCellTowerLocationRequest {

    @SerializedName("cellTowers")
    @Expose
    private List<CellTower> cellTowers = null;

    public List<CellTower> getCellTowers() {
        return cellTowers;
    }

    public void setCellTowers(List<CellTower> cellTowers) {
        this.cellTowers = cellTowers;
    }

}