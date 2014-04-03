package model;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.rule.Rule;

public class RulesTracking {

    private List<String> tracking = new ArrayList<String>();

    public void track(Rule rule) {
        tracking.add("Name: " + rule.getName() + "; Salience: " + rule.getSalience());
    }

    public List<String> getTracking() {
        return tracking;
    }

}