import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class PerfectHashFunction {
    public ArrayList<String> keywordRead(){
        ArrayList<String> list = new ArrayList<String>();
        try {
            Scanner sc = new Scanner(new File("src/kywrdsOdd.txt"));
            while (sc.hasNext()){
                list.add(sc.next());
            }
            sc.close();
            System.out.println("Keyword List :" + list);

        }catch (FileNotFoundException e){
            //e.printStackTrace();
            System.out.println("File Not Found");
        }catch (Exception e){
            System.out.println("Error occur");
        }
        if (list.isEmpty()){
            System.out.println("An Empty List");
        }
        return list;
    }

    public String[] construcFuntion(){

        ArrayList<String> list = keywordRead();
        ArrayList<String> charList = new ArrayList<>();

        //Adding first and last characters of each keyword to the charList
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            String firstletter = String.valueOf(s.charAt(0));
            String lastletter = String.valueOf(s.charAt(s.length() - 1));
            charList.add(firstletter);
            charList.add(lastletter);

        }
        //Count frequency of characters and add to the HashMap
        HashMap<String, Integer> map = new HashMap<>();
        for (String i : charList) {
            Integer j = map.get(i);
            map.put(i, (j == null) ? 1 : j + 1);
        }
        System.out.println("Character value Map :" + map);

        //Get sum of first and last character values and adding to the charSum HashMap
        HashMap<String, Integer> charSum = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            String let = list.get(i);
            int count = map.get(String.valueOf(let.charAt(0))) + map.get(String.valueOf(let.charAt(let.length() - 1)));
            charSum.put(let, count);
        }
        System.out.println("Character Map :" + charSum);

        //Sorting charSum hashmap
        //Adding sort values to the hashmap
        Map<String, Integer> sortedMap = charSum.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println("Decending Order : " + sortedMap);

        //Set G values to 0 and add to the gValue HastMap
        HashMap<String, Integer> gValue = new HashMap<>();
        for (String gVal : map.keySet()){
            gValue.put(gVal,0);
        }
        System.out.println("G Values (zero) : " + gValue);


        String[] insertArray = new String[list.size()];
        String[] strArray = sortedMap.keySet().toArray(new String[0]);
        ArrayList<Character> used = new ArrayList<>();

        //Call getInsertionValue method and get values one by one using for loop
        for (String sortedKeys : sortedMap.keySet()){
            int frequencyValue = getInsertionValue(sortedKeys, gValue, list.size(), insertArray,strArray, used);
            insertArray[frequencyValue] = sortedKeys;
        }

        return insertArray;
    }

    public static int getInsertionValue(String sortedKeys, HashMap<String, Integer>gValue, int listSize, String[] insertArray, String[] strArray, ArrayList<Character>used){
        int length = sortedKeys.length();                                                //Word length of keyword
        int gFirstChar = gValue.get(String.valueOf(sortedKeys.charAt(0)));               //First Character value
        String fl = String.valueOf(sortedKeys.charAt(sortedKeys.length() - 1));          //Last Character value
        String key = fl;
        int gLastChar = gValue.get(key);
        int hValue = 0;                                                                 // H value
        int maxValue = 4;                                                              //Max Value
        int inValue = 0;
        String newVal = null;

        if (!used.contains(sortedKeys.charAt(0)) || !used.contains(sortedKeys.charAt(sortedKeys.length() - 1))){
            hValue = (length + gFirstChar + gLastChar) % listSize;
            inValue = gFirstChar;
            newVal = String.valueOf(sortedKeys.charAt(0));
        }
        else if (used.contains(sortedKeys.charAt(0))){
            inValue = gLastChar;
            newVal = fl;
        }
        if (insertArray[hValue] != null && inValue <= maxValue ) {

            gValue.put(newVal, inValue + 1);
            hValue = getInsertionValue(sortedKeys, gValue, listSize, insertArray,strArray,used);

        }
        else if (insertArray[hValue] != null && inValue > maxValue) {
            int previous = (Arrays.asList(strArray).indexOf(sortedKeys)) - 1;
            try {
                String prevElement = strArray[previous];
                gValue.put(newVal, inValue + 1);
                hValue = getInsertionValue(prevElement, gValue, length, insertArray, strArray,used);

                insertArray[hValue] = prevElement;
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
            }
        }
        used.add(sortedKeys.charAt(0));
        return hValue;
    }

    public Hashtable<String, Integer> constructHashTable(){

        String[] insertArray = construcFuntion();

        //Construct Hash Table
        Hashtable<String, Integer> hashTable = new Hashtable<>();
        System.out.println();
        for (int i = 0; i < insertArray.length; i++) {
            hashTable.put(insertArray[i], i);
        }
        System.out.println("Hash Table : " + hashTable);
        return hashTable;
    }

    public void testWordRead() throws IOException {
        //Get start time
        long startTime = System.currentTimeMillis();
        ArrayList<String> test = new ArrayList<>();
        HashMap<String, Integer> testWordMap = new HashMap<>();

        int noOfLine = 0;
        int noOfWord = 0;

        // Read test keyword file
        File file = new File("src/tstOdd.txt");
        if (file.exists()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String textLine;
            while ((textLine = br.readLine()) != null) {
                textLine = textLine
                        .replaceAll("[(,.?)]", "")
                        .replaceAll("\\d+", "")
                        .replaceAll("-", ",")
                        .replaceAll("=", "");

                String[] newString = textLine.split("\\s+");
                if (textLine.length() != 0){
                    noOfLine++;                             //Count number of lines
                }

                for (String word : newString) {
                    if (word.length() > 0) {
                        test.add(word);
                        noOfWord++;                     //Count number of words
                    }

                    if (!testWordMap.containsKey(word)) {
                        testWordMap.put(word, 1);
                    } else {
                        testWordMap.put(word, testWordMap.get(word) + 1);
                    }
                }
            }
            fr.close();
        } else {
            System.out.println("File Doesn't Exists");
        }
        if (testWordMap.isEmpty()){
            System.out.println("File is Empty");
        }

        Hashtable<String, Integer> hashTable = constructHashTable();
        HashMap<String, Integer> countResult = new HashMap<>();

        for (String st : testWordMap.keySet()) {

            if (hashTable.containsKey(st)){
                countResult.put(st,testWordMap.get(st));
            }
        }
        //Count Total keywords
        int totalCount = 0;
        for (String count : countResult.keySet()){
            totalCount = totalCount + countResult.get(count);
        }
        //Get end time
        long endTime = System.currentTimeMillis();

        //Print final statistics
        System.out.println("Final Result :" + countResult);
        System.out.println();
        System.out.println("\t\t\tStatistic Results: ");
        System.out.println("--------------------------------------");
        System.out.println("Total Line Read : " + noOfLine);
        System.out.println("Total Words Read : " + noOfWord);
        System.out.println("\tBreakdown by Keyword");
        for (String i : countResult.keySet()){
            System.out.println("\t\t" + i + " : " + countResult.get(i));
        }
        System.out.println("Total Keywords : " + totalCount);
        System.out.println("Execution time : " + (endTime - startTime) + " milliseconds");
    }

}

