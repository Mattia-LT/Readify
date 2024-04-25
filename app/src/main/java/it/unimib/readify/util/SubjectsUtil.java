package it.unimib.readify.util;

import static android.view.View.NO_ID;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import it.unimib.readify.R;

public class SubjectsUtil {
    private static SubjectsUtil instance;

    private Map<Integer, String> subjectsApiValueMap;

    private int[] chipIdList;
    private final Context context;

    private SubjectsUtil(Context context){
        this.context = context.getApplicationContext();
        initSubjectsHashMap();
    }

    public static SubjectsUtil getSubjectsUtil(Context context){
        if (instance == null) {
            instance = new SubjectsUtil(context);
        }
        return instance;
    }

    private void initSubjectsHashMap() {
        subjectsApiValueMap = new LinkedHashMap<>();
        String[] apiValues = new String[]{
                "fiction_action_&_adventure",
                "biography",
                "classic_literature",
                "comic_books_strips",
                "drama",
                "fiction_fantasy_general",
                "fiction_historical_general",
                "fiction_horror",
                "humor",
                "fiction_mystery_&_detective_general",
                "poetry",
                "fiction_romance_general",
                "fiction_science_fiction_general",
                "fiction_short_stories_(single_author)",
                "sports",
                "fiction_thrillers_general"
        };

        Resources res = context.getResources();
        chipIdList =
                Arrays.stream(res.getStringArray(R.array.chip_genres_values))
                .mapToInt(Integer::parseInt)
                .toArray();
        for(int i = 0; i < chipIdList.length; i++) {
            int key = chipIdList[i];
            String value = apiValues[i];
            subjectsApiValueMap.put(key, value);
        }
    }

    public int[] getChipIdList() {
        return chipIdList;
    }

    public boolean containSubject(String subject){
        for (Map.Entry<Integer, String> entry : subjectsApiValueMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }

    public String getApiValue(String subject){
        Resources res = context.getResources();
        String[] subjectList = res.getStringArray(R.array.chip_genres);

        for(int i = 0; i < chipIdList.length; i++){
            if(subjectList[i].equalsIgnoreCase(subject)){
                return subjectsApiValueMap.get(chipIdList[i]);
            }
        }
        return null;
    }

    public int getChipId(String apiValue) {
        Log.e("ENTRY api value", apiValue);
        for (Map.Entry<Integer, String> entry : subjectsApiValueMap.entrySet()) {
            Log.e("ENTRY GET VALUE", entry.getValue());
            if (entry.getValue().equalsIgnoreCase(apiValue)) {
                return entry.getKey();
            }
        }
        return NO_ID;
    }
}