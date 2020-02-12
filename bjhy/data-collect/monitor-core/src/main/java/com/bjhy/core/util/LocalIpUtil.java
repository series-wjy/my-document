package com.bjhy.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public class LocalIpUtil {

     //通过截取cmd流方式得到计算机的配置信息(不好)  
    public static List<String> getIpAddress(){
        Process p = null;  
        List<String> address  = new ArrayList<String>();
        try{  
            p = new ProcessBuilder("ipconfig", "/all").start();
        }catch(Exception e){  
            return address;  
        }  
        StringBuffer sb = new StringBuffer();  
        //读取进程输出值  
        InputStream inputStream = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";  
        try{  
            while((s=br.readLine())!=null){  
                sb.append(s+"\n");  
            }  
        }catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            try{  
                inputStream.close();  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
        }  
        System.out.println(sb);  
        return address;  
    }  

    public static void getIpconfig(){  
        Map<String,String> map = System.getenv();
        System.out.println(map.get("USERNAME"));//获取用户名  
        System.out.println(map.get("COMPUTERNAME"));//获取计算机名  
        System.out.println(map.get("USERDOMAIN"));//获取计算机域名  
    }  
    //得到计算机的ip地址和mac地址  
    public static void getConfig(){  
        try{  
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            //ni.getInetAddresses().nextElement().getAddress();  
            byte[] mac = ni.getHardwareAddress();  
            String sIP = address.getHostAddress();  
            String sMAC = "";  
            Formatter formatter = new Formatter();  
            for (int i = 0; i < mac.length; i++) {  
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i],
                        (i < mac.length - 1) ? "-" : "").toString();  

            }  
            System.out.println("IP：" + sIP);  
            System.out.println("MAC：" + sMAC);  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    //得到计算机的ip,名称,操作系统名称,操作系统版本  
    public static void Config(){  
        try{  
            InetAddress addr = InetAddress.getLocalHost();   
            String ip=addr.getHostAddress().toString(); //获取本机ip  
            String hostName=addr.getHostName().toString(); //获取本机计算机名称  
            System.out.println("本机IP："+ip+"\n本机名称:"+hostName);  
            Properties props=System.getProperties();
            System.out.println("操作系统的名称："+props.getProperty("os.name"));  
            System.out.println("操作系统的版本："+props.getProperty("os.version"));   
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    //java环境  
    public static void all(){  
        Properties props=System.getProperties();  
        System.out.println("Java的运行环境版本："+props.getProperty("java.version"));  
        System.out.println("Java的运行环境供应商："+props.getProperty("java.vendor"));  
        System.out.println("Java供应商的URL："+props.getProperty("java.vendor.url"));  
        System.out.println("Java的安装路径："+props.getProperty("java.home"));  
        System.out.println("Java的虚拟机规范版本："+props.getProperty("java.vm.specification.version"));  
        System.out.println("Java的虚拟机规范供应商："+props.getProperty("java.vm.specification.vendor"));  
        System.out.println("Java的虚拟机规范名称："+props.getProperty("java.vm.specification.name"));  
        System.out.println("Java的虚拟机实现版本："+props.getProperty("java.vm.version"));  
        System.out.println("Java的虚拟机实现供应商："+props.getProperty("java.vm.vendor"));  
        System.out.println("Java的虚拟机实现名称："+props.getProperty("java.vm.name"));  
        System.out.println("Java运行时环境规范版本："+props.getProperty("java.specification.version"));  
        System.out.println("Java运行时环境规范供应商："+props.getProperty("java.specification.vender"));  
        System.out.println("Java运行时环境规范名称："+props.getProperty("java.specification.name"));  
        System.out.println("Java的类格式版本号："+props.getProperty("java.class.version"));  
        System.out.println("Java的类路径："+props.getProperty("java.class.path"));  
        System.out.println("加载库时搜索的路径列表："+props.getProperty("java.library.path"));  
        System.out.println("默认的临时文件路径："+props.getProperty("java.io.tmpdir"));  
        System.out.println("一个或多个扩展目录的路径："+props.getProperty("java.ext.dirs"));  
        System.out.println("操作系统的名称："+props.getProperty("os.name"));  
        System.out.println("操作系统的构架："+props.getProperty("os.arch"));  
        System.out.println("操作系统的版本："+props.getProperty("os.version"));  
        System.out.println("文件分隔符："+props.getProperty("file.separator"));//在 unix 系统中是＂／＂ System.out.println("路径分隔符："+props.getProperty("path.separator"));//在 unix 系统中是＂:＂ System.out.println("行分隔符："+props.getProperty("line.separator"));//在 unix 系统中是＂/n＂ System.out.println("用户的账户名称："+props.getProperty("user.name"));  
        System.out.println("用户的主目录："+props.getProperty("user.home"));  
        System.out.println("用户的当前工作目录："+props.getProperty("user.dir"));  
    }  
    public static void main(String[] args) throws UnknownHostException, SocketException {
        //all();
        //getIpAddress();
        InetAddress address1 = InetAddress.getLocalHost();

        Optional<Inet4Address> opt = getLocalIp4Address();
        Inet4Address address2 = opt.get();
        System.out.println(address2.getHostAddress());
    }

    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        if (e == null) {
            return addresses;
        }
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            if (!isValidInterface(n)) {
                continue;
            }
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                if (isValidAddress(i)) {
                    addresses.add((Inet4Address) i);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     *
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    private static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public static Optional<Inet4Address> getLocalIp4Address() throws SocketException {
        final List<Inet4Address> ipByNi = getLocalIp4AddressFromNetworkInterface();
        if (ipByNi.isEmpty() || ipByNi.size() > 1) {
            final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
            if (ipBySocketOpt.isPresent()) {
                return ipBySocketOpt;
            } else {
                return ipByNi.isEmpty() ? Optional.empty() : Optional.of(ipByNi.get(0));
            }
        }
        return Optional.of(ipByNi.get(0));
    }

}  