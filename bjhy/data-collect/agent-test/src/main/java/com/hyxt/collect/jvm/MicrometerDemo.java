package com.hyxt.collect.jvm;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @ClassName MicrometerDemo.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月23日 13:35:00
 */
public class MicrometerDemo {

    private SimpleMeterRegistry registry = new SimpleMeterRegistry();

    public static void main(String[] args) {
        MicrometerDemo demo = new MicrometerDemo();
        MeterRegistry registry = new SimpleMeterRegistry();
        JvmMemoryMetrics a = new JvmMemoryMetrics();
        a.bindTo(registry);
        Collection<Meter> meters = registry.find("jvm.memory.used").tags(demo.parseTags(new ArrayList<String>())).meters();
        Map<Statistic, Double> samples = demo.getSamples(meters);
        Map<String, Set<String>> availableTags = demo.getAvailableTags(meters);
        System.out.println(samples);
        System.out.println(availableTags);

        List<Tag> tags = demo.parseTags(new ArrayList<String>(){
            {
                add("id:PS Survivor Space");
                add("area:heap");
            }
        });
        Collection<Meter> meters2 = registry.find("jvm.memory.used").tags(tags).meters();
        Map<Statistic, Double> samples2 = demo.getSamples(meters2);
        Map<String, Set<String>> availableTags2 = demo.getAvailableTags(meters2);
        tags.forEach((t) -> availableTags.remove(t.getKey()));
        System.out.println(samples2);
        System.out.println(availableTags2);
    }

    private Map<Statistic, Double> getSamples(Collection<Meter> meters) {
        Map<Statistic, Double> samples = new LinkedHashMap<>();
        meters.forEach((meter) -> mergeMeasurements(samples, meter));
        return samples;
    }

    private Map<String, Set<String>> getAvailableTags(Collection<Meter> meters) {
        Map<String, Set<String>> availableTags = new HashMap<>();
        meters.forEach((meter) -> mergeAvailableTags(availableTags, meter));
        return availableTags;
    }

    private void mergeAvailableTags(Map<String, Set<String>> availableTags, Meter meter) {
        meter.getId().getTags().forEach((tag) -> {
            Set<String> value = Collections.singleton(tag.getValue());
            availableTags.merge(tag.getKey(), value, this::merge);
        });
    }

    private <T> Set<T> merge(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1.size() + set2.size());
        result.addAll(set1);
        result.addAll(set2);
        return result;
    }

    private void mergeMeasurements(Map<Statistic, Double> samples, Meter meter) {
        meter.measure().forEach((measurement) -> samples.merge(measurement.getStatistic(), measurement.getValue(),
                mergeFunction(measurement.getStatistic())));
    }

    private BiFunction<Double, Double, Double> mergeFunction(Statistic statistic) {
        return Statistic.MAX.equals(statistic) ? Double::max : Double::sum;
    }

    private List<Tag> parseTags(List<String> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(this::parseTag).collect(Collectors.toList());
    }

    private Tag parseTag(String tag) {
        String[] parts = tag.split(":", 2);
        if (parts.length != 2) {
            throw new RuntimeException("Each tag parameter must be in the form 'key:value' but was: " + tag);
        }
        return Tag.of(parts[0], parts[1]);
    }
}
