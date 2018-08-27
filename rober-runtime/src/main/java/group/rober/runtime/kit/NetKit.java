package group.rober.runtime.kit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class NetKit {

    public static InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public static String getHostAddress(){
        try{
            return getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            return "";
        }
    }

    public static String getHostName(){
        try{
            return getLocalHost().getHostName();
        }catch(UnknownHostException e){
            return "";
        }
    }

    /**
     * 获取所有的网卡信息
     * @return
     * @throws SocketException
     */
    public static List<InetAddress> getInetAddressList() throws SocketException {
        List<InetAddress> retList = new ArrayList<>();

        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()){
            NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
            Enumeration address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress inetAddress = (InetAddress) address.nextElement();
                String netAdpterName = "未知网卡";
                String netIp = null;
                // 外网IP
                if (!inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                    netAdpterName = "外网-地址";
                    netIp = inetAddress.getHostAddress();
                    // 内网IP
                } else if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                    netAdpterName = "内网-地址";
                    netIp = inetAddress.getHostAddress();
                }
                if(netIp == null)continue;

                retList.add(inetAddress);
            }
        }

        return retList;

    }
}
