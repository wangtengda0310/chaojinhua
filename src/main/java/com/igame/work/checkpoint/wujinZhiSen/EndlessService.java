package com.igame.work.checkpoint.wujinZhiSen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndlessService {
    public Map<Long, Integer> tempBufferId = new ConcurrentHashMap<>();//临时ID
}
