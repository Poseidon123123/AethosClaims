package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;

import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static ChunkHolder getHighestHolder(HashMap<ChunkHolder, Integer> map) {
        ChunkHolder maxKey = null;
        Integer maxValue = null;
        for (Map.Entry<ChunkHolder, Integer> entry2 : map.entrySet()) {
            ChunkHolder key = entry2.getKey();
            Integer value = entry2.getValue();

            if (maxValue == null || value.compareTo(maxValue) > 0) {
                maxValue = value;
                maxKey = key;
            }
        }
        return maxKey;
    }
}
