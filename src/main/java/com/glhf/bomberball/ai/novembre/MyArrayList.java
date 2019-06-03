package com.glhf.bomberball.ai.novembre;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyArrayList<T> extends ArrayList<T> {

    MyArrayList(){
        super();
    }

    public MyArrayList clone(){
        MyArrayList ret = new MyArrayList();
        Iterator<T> it = this.iterator();
        while (it.hasNext()){
            T obj = it.next();
            T obj2;
            if(obj instanceof MyArrayList){
                ret.add(((MyArrayList)obj).clone());
            }else{
                ret.add(obj);
            }

        }
        return ret;
    }

    MyArrayList(List<T> array){
        for (T elem:array){
            this.add(elem);
        }
    }

    public MyArrayList<T> keepFirstN(int n){
        MyArrayList<T> newArray = new MyArrayList<T>();
        for (int i =0;i<5;i++){
            if(this.size()>i){
                newArray.add(this.get(i));
            }
        }
        return newArray;
    }
}
