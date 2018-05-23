/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.JOptionPane;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * @author Raul
 */
public enum OS {

    WINDOWS("windows"), LINUX("linux");

    public static final OS OS;

    //private static final DGLog logger = new DGLog(br.com.digicon.dnserver.util.OS.class);
    private String name;

    static {
        String tempOS = System.getProperty("os.name");

        if (tempOS == null) {
            tempOS = "N/A";
        } else {
            tempOS = tempOS.trim().toLowerCase();
        }

        if (tempOS.contains(WINDOWS.name)) {
            OS = WINDOWS;
        } else if (tempOS.contains(LINUX.name)) {
            OS = LINUX;
        } else {
            OS = null;
            JOptionPane.showMessageDialog(null, "O sistema operacional não é compatível a aplicação.", "Aviso do Sistema", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    OS(String os) {
        name = os;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
   //===================================================================
    /**
     * Ao contrário do que muitos pensam, é possível criptografar um texto e
     * descriptografá-lo em seguida e o melhor é que existe uma biblioteca que
     * nos permite fazê-lo de forma rápida, simples e objetiva. Todos sabem que
     * criar um algoritmo para criptografia não é nada simples e se pensarmos em
     * descriptografar o que criptografamos, este algoritmo ficaria mais
     * complicado ainda, porém encontramos esta biblioteca que nos permite
     * fazê-lo de forma muito simples. Primeiro baixe a biblioteca Jasypt:
     * http://www.jasypt.org/
     *
     * A primeira coisa é instanciar a classe BasicTextEncryptor
     * (org.jasypt.util.text.BasicTextEncryptor):
     *
     * BasicTextEncryptor bte = new BasicTextEncryptor();
     *
     * Em posse da instância de BasicTextEncryptor, basta utilizar seus métodos:
     * setPassword(String password) decrypt(String encryptedMessage)
     * encrypt(String message)
     *
     * @param textoSemCriptografia
     * @return
     */
    public static String Encriptar(String textoSemCriptografia) {

        //System.out.println("Texto sem criptografia: " + textoSemCriptografia);
        //instanciamos a classe BasicTextEncryptor
        BasicTextEncryptor bte = new BasicTextEncryptor();

        //inserimos uma senha qualquer: A senha que voce quizer
        bte.setPassword("meUDEus!!!");

        //criamos uma String que recebe a senha criptografada
        String textoComCriptografia = bte.encrypt(textoSemCriptografia);
        //System.out.println("Seu texto criptografado = " + textoComCriptografia);

        return textoComCriptografia;
    }

    //===================================================================
    /**
     * Ao contrário do que muitos pensam, é possível criptografar um texto e
     * descriptografá-lo em seguida e o melhor é que existe uma biblioteca que
     * nos permite fazê-lo de forma rápida, simples e objetiva. Todos sabem que
     * criar um algoritmo para criptografia não é nada simples e se pensarmos em
     * descriptografar o que criptografamos, este algoritmo ficaria mais
     * complicado ainda, porém encontramos esta biblioteca que nos permite
     * fazê-lo de forma muito simples. Primeiro baixe a biblioteca Jasypt:
     * http://www.jasypt.org/
     *
     * A primeira coisa é instanciar a classe BasicTextEncryptor
     * (org.jasypt.util.text.BasicTextEncryptor):
     *
     * BasicTextEncryptor bte = new BasicTextEncryptor();
     *
     * Em posse da instância de BasicTextEncryptor, basta utilizar seus métodos:
     * setPassword(String password) decrypt(String encryptedMessage)
     * encrypt(String message)
     *
     * @param textoSemCriptografia
     * @return
     */
    public static String Descriptografar(String textoComCriptografia) {

        //instanciamos a classe BasicTextEncryptor
        BasicTextEncryptor bte = new BasicTextEncryptor();

        //inserimos uma senha qualquer: A senha que voce quizer
        bte.setPassword("meUDEus!!!");

        //criamos uma String que recebe a senha descriptografada
        String textoSemCriptografia = bte.decrypt(textoComCriptografia);
        //System.out.println("Texto descriptografado  = " + textoSemCriptografia);

        return textoSemCriptografia;
    }    
}
