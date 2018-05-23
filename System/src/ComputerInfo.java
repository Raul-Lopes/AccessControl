
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.StringTokenizer;

//Java - Como obter o id do computador em Windows e Linux Ubuntu
//==============================================================
//
//Alguns aplicativos dos tipos desktop, serviço ou licenciadores baseados em processamento 
//fortemente identificado sempre requerem do programador desafios novos para pegar a identidade física do computador.
//
//Quando surge o problema de pegar uma identidade sempre vem a cabeça do programador mais 
//experiente as seguintes perguntas seguidas por mais perguntas:
//
//1) Se eu pegar o macaddress da placa de rede?
//2) Mas se a placa de rede queimar ou for trocada de posição?
//3) Posso obter o número mais seguro?
//4) Será este número o código de série da CPU porque o FCC cuida para não haver 
//repetição de serial, ok?
//5) Mas a máquina pode ser virtual e copiada, o macaddress e código da CPU podem 
//ser clonados e assim o FCC perder controle?
//6) Talvez o nome da máquina possar ser menos sujeito a alteração e se tiver clone, 
//não vai funcionar na mesma rede, mas será que numa mudança de política de 
//infra-estrutura não vão mudar o nome da máquina legalmente?
//7) Como faço para saber se a máquina é virtual?
//
//Não existe uma fórmula mágica para impedir as alterações das identidades de uma máquina, 
//até de seu processador porque pode ser trocado.
//
//Em caso de máquinas virtuais o problema é agravado mais ainda porque elas podem 
//ser clonadas e funcionarem com identidades idênticas em redes (LAN) diferentes.
//
//Por causa disso a única forma segura de garantir uma identidade única é resolvida 
//através de tags ou tokens em forma de hardware para imprimir uma identidade imutável, 
//no entanto, este método pode ser bastante caro e ainda reduzir bastante a atratividade 
//de seu aplicativo num cenário mundial onde as pessoas buscam fazer as coisas no menor tempo, 
//da forma mais simples e mais barata.
//
//Mas não se desespere, para a maior parte dos casos, obter a identidade na primeira 
//execução do software e persistir em um arquivo, registro do sistema operacional ou banco 
//de dados de maneira ofuscada já é o suficiente para congelar e tornar imutável o conjunto 
//de identidades da máquina. Esta técnica pode ser chamada de congelar ou tirar um snapshot 
//dos identificadores da máquina para não sofrerem uma crise caso uma identidade utilizada 
//para validação de sua aplicação seja legalmente mudada por razões diversas.
//
//A classe a seguir se chama ComputerInfo e possui métodos para obter o número de série da CPU, 
//endereço IP do computador, nome do computador, macaddress do primeiro adaptador de rede e se é 
//um adaptador virtual. Não é objetivo discutir a utilização desta classe porque isso vai variar 
//da necessidade de solução de cada um.
//fonte: http://oracle2java.blogspot.ie/2013/09/java-como-obter-o-id-do-computador-em.html
public class ComputerInfo {

    public static final String NO_CPU_ID = "XXXXXXXXXX";

    private static ComputerInfo ci = null;

    private String cpuSerial;
    private String hostName;
    private String macAddress;
    private String hostAddress;
    private Boolean virtualAdapter;

    private ComputerInfo() {

        try {

            NetworkInterface netInter = null;

            if (OS.OS == OS.WINDOWS) {
                InetAddress localHost = InetAddress.getLocalHost();
                netInter = NetworkInterface.getByInetAddress(localHost);
            } else if (OS.OS == OS.LINUX) {
                for (NetworkInterface n : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    netInter = n;
                    break;
                }
            }

            byte[] macAddressBytes = netInter.getHardwareAddress();

            String macAddress = "null";

            // Verifica se obteve macaddress
            if (macAddressBytes != null && macAddressBytes.length > 5) {

                macAddress = String.format(
                        "%1$02x%2$02x%3$02x%4$02x%5$02x%6$02x", macAddressBytes[0],
                        macAddressBytes[1], macAddressBytes[2], macAddressBytes[3],
                        macAddressBytes[4], macAddressBytes[5]
                ).toUpperCase();
            }

            InetAddress addr = InetAddress.getLocalHost();

            this.cpuSerial = getCPUID();
            this.hostName = addr.getHostName();
            this.macAddress = macAddress;
            this.hostAddress = addr.getHostAddress();
            this.virtualAdapter = netInter.isVirtual();

        } catch (UnknownHostException | SocketException e) {
            System.err.println(e);
        }

    }

    public static synchronized ComputerInfo getIntance() {

        if (ci == null) {
            ci = new ComputerInfo();
        }

        return ci;

    }

    public String getCpuSerialB() {
        return cpuSerial;
    }

    public String getHostName() {
        return hostName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Boolean isVirtualAdapter() {
        return virtualAdapter;
    }

    public static String getCPUID() {

        String result = null;

        if (OS.OS == OS.WINDOWS) {
            result = getCPUIDForWindows();
        } else if (OS.OS == OS.LINUX) {
            result = getCPUIDForLinux();
        }

        return result;

    }

    private static String getCPUIDForWindows() {

        StringBuffer result = new StringBuffer();

        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            StringBuilder vbs = new StringBuilder();
            vbs.append("On Error Resume Next \r\n\r\n");
            vbs.append("strComputer = \".\"  \r\n");
            vbs.append("Set objWMIService = GetObject(\"winmgmts:\" _ \r\n");
            vbs.append("    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n");
            vbs.append("Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n ");
            vbs.append("For Each objItem in colItems\r\n ");
            vbs.append("    Wscript.Echo objItem.ProcessorId  \r\n ");
            vbs.append("    exit for  ' do the first cpu only! \r\n");
            vbs.append("Next                    ");

            fw.write(vbs.toString());
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
            input.close();
        } catch (IOException ex) {
            //Loggin.logDiarioExcecao(ex, true);

        }

        if (result == null || result.toString().trim().length() < 1) {
            result = new StringBuffer();
            return NO_CPU_ID;
        }

        return result.toString().trim();

    }

    private static String getCPUIDForLinux() {

        String result = null;

        try {

            Process p = Runtime.getRuntime().exec("dmidecode -t 4");

            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                
                while ((line = input.readLine()) != null) {
                    if (line.length() > 3 && line.substring(0, 3).equals(("ID:"))) {
                        StringTokenizer tokenizer = new StringTokenizer(line, ":");
                        line = tokenizer.nextToken();
                        result = tokenizer.nextToken();
                    }
                }
            }

            if (result == null || result.trim().length() == 0) {
                result = NO_CPU_ID;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return result.trim();
    }

//    Uma obeservação importante é que, se você quer utilizar para colocar travas 
//    em softwares este é o número que você vai precisar, pois o que tenho 
//    visto por aí é o pessoal pesquisando por "Número do Volume do HD" 
//    que muda quando é criado um novo volume tipo "Unidade D:", 
//    mas o Serial do HD, este não muda, assim pode ser usado para qualquer fim. 
//    Não achei nenhuma solução no fórum e as que achei eram complicadas e pagas, 
//    por isto estou postando aqui. Este foi pego no fórum da Sun em Inglês.
    public String getHDSerial(String drive) {
        String result = "";
        try {
            //File file = File.createTempFile("tmp",".vbs");  
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                        + "Set colDrives = objFSO.Drives\n"
                        + "Set objDrive = colDrives.item(\""
                        + drive
                        + "\")\n"
                        + "Wscript.Echo objDrive.SerialNumber";
                
                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result += line;
                }
            }
        } catch (IOException ex) {
            //Loggin.logDiarioExcecao(ex, true);

        }
        if (result.trim().length() < 1 || result == null) {
            result = "NO_DISK_ID";

        }

        return result.trim();
    }

    public String getCpuSerialA() {

        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs
                    = "On Error Resume Next \r\n\r\n"
                    + "strComputer = \".\"  \r\n"
                    + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                    + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                    + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                    + "For Each objItem in colItems\r\n "
                    + "    Wscript.Echo objItem.ProcessorId  \r\n "
                    + "    exit for  ' do the first cpu only! \r\n"
                    + "Next                    ";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (IOException ex) {
            //Loggin.logDiarioExcecao(ex, true);

        }
        if (result.trim().length() < 1 || result == null) {
            result = "NO_CPU_ID";
        }
        return result.trim();
    }

}
