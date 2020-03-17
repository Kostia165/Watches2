package com.example.watches2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Subject{

    private List<HashMap<String, Observer>> observers = new ArrayList<>();

    public void attach(Observer observer, String action){
        HashMap<String, Observer> newData = new HashMap<String, Observer>();
        newData.put(action, observer);
        observers.add(newData);
    }

    public void notifyObservers(String action){

        for (HashMap<String, Observer> map : observers) {
            if(map.containsKey(action)){
                map.get(action).act();
            }
        }
    }

}
