package com.hyxt.collect.jvm;

import java.lang.management.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JvmMemoryInfo2.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年12月23日 17:12:00
 */
public class JvmMemoryInfo2 {
    public static void main(String[] args) {
        TransportJvm transportJvm = new TransportJvm();
        transportJvm.setServerInstanceId("");
        transportJvm.setServerName("");
        transportJvm.setIp("");
        transportJvm.setHostname("");
        //================jvm线程信息================
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        //获取当前JVM内的线程数量，该指标非常重要。
        transportJvm.setThreadCount(threadBean.getThreadCount());
        transportJvm.setThreadCpuTime(threadBean.getCurrentThreadCpuTime());
        transportJvm.setThreadUserTime(threadBean.getCurrentThreadUserTime());
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
        //================jvm类加载信息================
        //获取当前JVM加载的类数量
        transportJvm.setLoadedClassCount(classLoadingBean.getLoadedClassCount());
        //获取JVM总加载的类数量
        transportJvm.setTotalLoadedClassCount(classLoadingBean.getTotalLoadedClassCount());
        //获取JVM卸载的类数量
        transportJvm.setUnloadedClassCount(classLoadingBean.getUnloadedClassCount());
        //================jvm的cpu信息================
        OperatingSystemMXBean operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
        //获取服务器的CPU个数
        transportJvm.setAvailableProcessors(operatingSystemBean.getAvailableProcessors());
        //获取服务器的平均负载。这个指标非常重要，它可以有效的说明当前机器的性能是否正常，如果load过高，说明CPU无法及时处理任务。
        transportJvm.setSystemLoadAverage(operatingSystemBean.getSystemLoadAverage());
        //================jvm的堆内存状态================
        //这里会返回老年代，新生代等内存区的使用情况，按需自取就好
        List<MemoryPoolMXBean> memoryPoolBeans = ManagementFactory.getMemoryPoolMXBeans();
        memoryPoolBeans.forEach(memoryPoolMXBean -> {
            transportJvm.getHeapMemoryInfo().put(memoryPoolMXBean.getName().replaceAll(" ", ""), memoryPoolMXBean.getUsage());
        });
        //================jvm的内存状态================
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        //获取堆内存使用情况，包括初始大小，最大大小，已使用大小等，单位字节
        transportJvm.setHeapMemoryUsage(memoryBean.getHeapMemoryUsage());
        //获取堆外内存使用情况。
        transportJvm.setNonHeapMemoryUsage(memoryBean.getNonHeapMemoryUsage());
        System.out.println(transportJvm.getContent());
    }

    interface Transportor {
        /**
         * 内容.
         * @return
         */
        String getContent();
    }

    static class TransportJvm implements Transportor {
        private String serverInstanceId;
        private String serverName;
        private String hostname;
        private String ip;
        private Date createTime = new Date();

        private int threadCount;
        private long threadCpuTime;
        private long threadUserTime;

        private long loadedClassCount;
        private long totalLoadedClassCount;
        private long unloadedClassCount;

        private int availableProcessors;
        private double systemLoadAverage;

        private Map<String, MemoryUsage> heapMemoryInfo = new HashMap<>();

        private MemoryUsage heapMemoryUsage;
        private MemoryUsage nonHeapMemoryUsage;

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getServerInstanceId() {
            return serverInstanceId;
        }

        public void setServerInstanceId(String serverInstanceId) {
            this.serverInstanceId = serverInstanceId;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public long getThreadCpuTime() {
            return threadCpuTime;
        }

        public void setThreadCpuTime(long threadCpuTime) {
            this.threadCpuTime = threadCpuTime;
        }

        public long getThreadUserTime() {
            return threadUserTime;
        }

        public void setThreadUserTime(long threadUserTime) {
            this.threadUserTime = threadUserTime;
        }

        public long getLoadedClassCount() {
            return loadedClassCount;
        }

        public void setLoadedClassCount(long loadedClassCount) {
            this.loadedClassCount = loadedClassCount;
        }

        public long getTotalLoadedClassCount() {
            return totalLoadedClassCount;
        }

        public void setTotalLoadedClassCount(long totalLoadedClassCount) {
            this.totalLoadedClassCount = totalLoadedClassCount;
        }

        public long getUnloadedClassCount() {
            return unloadedClassCount;
        }

        public void setUnloadedClassCount(long unloadedClassCount) {
            this.unloadedClassCount = unloadedClassCount;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        public void setAvailableProcessors(int availableProcessors) {
            this.availableProcessors = availableProcessors;
        }

        public double getSystemLoadAverage() {
            return systemLoadAverage;
        }

        public void setSystemLoadAverage(double systemLoadAverage) {
            this.systemLoadAverage = systemLoadAverage;
        }

        public MemoryUsage getHeapMemoryUsage() {
            return heapMemoryUsage;
        }

        public void setHeapMemoryUsage(MemoryUsage heapMemoryUsage) {
            this.heapMemoryUsage = heapMemoryUsage;
        }

        public MemoryUsage getNonHeapMemoryUsage() {
            return nonHeapMemoryUsage;
        }

        public void setNonHeapMemoryUsage(MemoryUsage nonHeapMemoryUsage) {
            this.nonHeapMemoryUsage = nonHeapMemoryUsage;
        }

        public Map<String, MemoryUsage> getHeapMemoryInfo() {
            return heapMemoryInfo;
        }

        public void setHeapMemoryInfo(Map<String, MemoryUsage> heapMemoryInfo) {
            this.heapMemoryInfo = heapMemoryInfo;
        }

        @Override
        public String getContent() {
            return GsonUtil.objectToJson(this);
        }
    }
}
