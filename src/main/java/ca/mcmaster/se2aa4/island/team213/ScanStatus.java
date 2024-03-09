package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScanStatus {
    JSONArray scanCreeks, scanSites, scanBiomes;

    public ScanStatus(JSONObject extraInfo) {
        this.scanCreeks = extraInfo.getJSONArray("creeks");
        this.scanSites = extraInfo.getJSONArray("sites");
        this.scanBiomes = extraInfo.getJSONArray("biomes");
    }
}
