/*
 * Copyright 2020 io.xream.sqli
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.sqli.cache.internal;

import io.xream.sqli.builder.Bb;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.KV;
import io.xream.sqli.builder.Sort;
import io.xream.sqli.cache.CriteriaCacheKeyBuildable;
import io.xream.sqli.util.SqliStringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Author Sim
 */
public class CriteriaCacheKeyBuilder implements CriteriaCacheKeyBuildable {

    @Override
    public String buildCacheKey(Criteria criteria, boolean isOfTotalRows){
        StringBuilder sb = new StringBuilder();
        sb.append(criteria.getRouteKey());
        buildCacheKeyByBbList(criteria.getBbList(), sb);

        if (! isOfTotalRows) {
            sb.append(criteria.getPage()).append(criteria.getRows());
            if (criteria.getSortList() != null) {
                for (Sort sort : criteria.getSortList()) {
                    sb.append(sort.getOrderBy()).append(sort.getDirection());
                }
            }
            if (criteria.getFixedSortList() != null) {
                for (KV kv : criteria.getFixedSortList()) {
                    sb.append(kv.k).append(kv.v);
                }
            }
        }
        return sb.toString();
    }

    private void buildCacheKeyByBbBlock(Bb bb, StringBuilder sb) {
        sb.append(bb.getC()).append(bb.getP());
        if (SqliStringUtil.isNotNull(bb.getKey()) || Objects.nonNull(bb.getValue())) {
            sb.append(bb.getKey()).append(bb.getValue());
        }
        List<Bb> subList = bb.getSubList();
        if (subList != null && !subList.isEmpty()){
            buildCacheKeyByBbList(subList,sb);
        }
    }

    private void buildCacheKeyByBbList(List<Bb> bbList, StringBuilder sb){
        int size = bbList.size();
        if (size == 0)
            return;
        if (size == 1){
            Bb bb = bbList.get(0);
            buildCacheKeyByBbBlock(bb,sb);
        }else{
            Set<Bb> set = new HashSet<>();
            for (Bb bb : bbList) {
                set.add(bb);
            }
            for (Bb bb : set){
                buildCacheKeyByBbBlock(bb,sb);
            }
        }

    }
}
