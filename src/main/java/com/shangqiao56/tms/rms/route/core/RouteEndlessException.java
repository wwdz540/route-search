package com.shangqiao56.tms.rms.route.core;

import java.util.List;

/**
 * 存在死循环路由
 */
public class RouteEndlessException extends RouteException {
    List<Path.NextItem> nextItems;

    public RouteEndlessException(String message, List<Path.NextItem> nextItems) {
        super(message);
        this.nextItems = nextItems;
    }

    public List<Path.NextItem> getNextItems() {
        return nextItems;
    }
}
