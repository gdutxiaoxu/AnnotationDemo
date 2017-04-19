package xj.jsonlibdemo;

import com.example.Seriable;

import java.util.List;

public class Person {
    @Seriable()
    String name;
    @Seriable()
    String area;
    @Seriable()
    int age;
    int weight;

    @Seriable()
    List<Article> mArticleList;
}
