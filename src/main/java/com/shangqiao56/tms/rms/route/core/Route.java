package com.shangqiao56.tms.rms.route.core;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private boolean status;
    private List<Path.NextItem> paths = new ArrayList<>();

    public void addPath(Path.NextItem path){
        paths.add(path);
    }

    public List<Path.NextItem> getPaths() {
        return paths;
    }
}
