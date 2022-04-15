package com.gblw.instrument.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * allOf 使用示例
 *
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月03日 22:43:00
 */
public class CompletableFutureAllofDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String content = "a";
        CompletableFuture<Long> future = countWebPageContent(content);
        System.out.println("include " + content + " page counts:" + future.get());
    }

    private static CompletableFuture<Long> countWebPageContent(String content) {
        String[] urls = {"baidu.com", "taobao.com", "jd.com"};
        List<String> webPageLinks = Arrays.asList(urls);
        List<Integer> res = new ArrayList<>();
        List<CompletableFuture<String>> pageContentFutures = webPageLinks
                .stream()
                .map(webpageLink -> downloadWebPage(webpageLink))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()]));

        CompletableFuture<List<String>> allPageContentsFutures = allFutures
                .thenApply(v -> pageContentFutures.stream().map(pageContentFuture -> {
                    try {
                        return pageContentFuture.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return "";
                }).collect(Collectors.toList()));
        CompletableFuture<Long> countFuture = allPageContentsFutures.thenApply(pageContents -> {
            return pageContents
                    .stream()
                    .filter(pageContent -> pageContent.contains(content))
                    .count();
        });
        return countFuture;
    }

    private static CompletableFuture<String> downloadWebPage(String url) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("download web page:" + url);
            // 模拟下载网页内容
            return UUID.randomUUID().toString();
        });
        return future;
    }
}
