package com.jay.gulistore.search.service;

import com.jay.gulistore.search.vo.SearchParam;
import com.jay.gulistore.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam param);
}
