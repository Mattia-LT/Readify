package it.unimib.readify.util;

import static android.view.View.NO_ID;

import android.content.Context;
import android.content.res.Resources;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import it.unimib.readify.R;

public class SubjectsUtil {

    /**
     * This class is used as a support for genre management. Since there are thousands of different genres
     * in the API we used, we took the main genres and used the methods contained in this class to handle
     * all the functionality of the application that relied on book genres.
     */

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

    /**
     * Initialize the selected subjects hashmap and assigns an id to each subject
     */

    private void initSubjectsHashMap() {
        subjectsApiValueMap = new LinkedHashMap<>();
        String[] apiValues = new String[]{
                "adventure",
                "biography",
                "classic",
                "comic",
                "drama",
                "fantasy",
                "historical",
                "horror",
                "humor",
                "mystery",
                "poetry",
                "romance",
                "science fiction",
                "short stories",
                "sports",
                "thriller"
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

    /**
     * @return an array of integers representing the ids of the chips
     */
    public int[] getChipIdList() {
        return chipIdList;
    }

    /**
     * @param subject - a string representing the name of the current subject
     * @return true if the subject is contained in the hashmap, false otherwise
     */
    public boolean containSubject(String subject){
        for (Map.Entry<Integer, String> entry : subjectsApiValueMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param subject - a string representing the name of the current subject (which can be of any type of supported locale)
     * @return a String containing the default value for that subject (english)
     */
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

    /**
     * @param apiValue - a string representing the default value of the current subject (the one supported by the api)
     * @return an integer representing the id of the corresponding chip
     */
    public int getChipId(String apiValue) {
        for (Map.Entry<Integer, String> entry : subjectsApiValueMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(apiValue)) {
                return entry.getKey();
            }
        }
        return NO_ID;
    }
}