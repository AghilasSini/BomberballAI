package com.glhf.bomberball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.glhf.bomberball.utils.Constants;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {

    private static HashMap<String, String> dictionary;
    private static String file_name;

    public static void load(String file_name){
        Translator.file_name=file_name;
        String path = Constants.PATH_TRANSLATIONS +file_name+".txt";
        dictionary = new HashMap<>();
        FileHandle br = Gdx.files.internal(path);
        Pattern pattern = Pattern.compile("^\\s*\"(.*)\"\\s*:\\s*\"(.*)\"\\s*");
        for(String line : br.readString().split("\n")) {
            line = line.replace("\r","");
            if(line.matches("^\\s*$") || line.matches("^#.*")) continue;
            Matcher m = pattern.matcher(line);
            if(m.find())
                if(!dictionary.containsKey(m.group(1))){
                    dictionary.put(m.group(1), m.group(2).replace("\\n","\n"));
                }else{
                    System.err.println("translation \""+m.group(1)+"\" is translated twice ! (you need to remove one)");
                }
            else
                System.err.println("cannot parse line : "+line);
        }
    }

    public static String translate(String key, Object... objects){
        if(!dictionary.containsKey(key)){
//            dictionary.put(key,key);
            System.err.println("\""+key+"\" unknown in translation file "+file_name+".txt");
            return "ERR "+key;
//            try {
//                FileWriter fw = new FileWriter(Constants.PATH_TRANSLATIONS +"en.txt");
//                for(String k : dictionary.keySet())
//                    fw.write("\""+k+"\"\t\t:\t\""+dictionary.get(k)+"\"\n");
//                fw.close();
//            } catch (IOException e) { e.printStackTrace(); }
        }

        return String.format(dictionary.get(key), objects);
    }

    public static String getInputName(String code){
        if(code==null)return "";
        int code_i = Integer.parseInt(code.substring(1));
        if(code.charAt(0)=='M'){
            if(code_i==0)return "Left Click";
            else if(code_i==1)return "Right Click";
            else if(code_i==2)return "Middle Click";
            else return "Mouse Button "+code_i;
        }else if (code.charAt(0)=='K'){
            return Input.Keys.toString(code_i);
        }else{
            assert false;
            return null;
        }
    }

}
