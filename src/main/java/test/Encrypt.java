package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
public class Encrypt {
    @Benchmark
    @Fork(value = 5)
    @Measurement(iterations = 10, time = 1)
    @Warmup(iterations = 5, time = 1)
    public void test(Encrypt encrypt) {
        encrypt(encrypt.data);
    }

    private static final int LOW_WATER_MARK = 32;
    private static final int HIGH_WATER_MARK = 126;

    public Encrypt() {
    }

    //@Param({"100", "10000", "1000000"})
    public int iterations = 1000000;

    String data = "[{\"even§t-name\":\"ft_ca§rdtxn\",\"eventsu§btype\":\"card§txn\",\"even§ttype\":\"f§t\",\"msg§Body\":\"{'b§in':'460134','chann§el':'POS','entry_mo§de':'071','mask_ca§rd_no':'NA','payee_id':'NA','dev§_owner_id':'CARDACCEPTOR','user§_type':'RE§TAIL','avl_bal':'000000000000','succ_fail_flg':'S','error_desc':'','chi§p_pin_flg':'N','resp_cde':'00','atm_intr_lim§it':'NA','mcc_code':'5999','term§inal_id':'TERMID01','txn_amt':'0000000001.00','pos_ecom_dom_limit':'NA','dev§ice_id':'TERMID01','acct_ownership':'CARD','pos_ecom_int§r_limit':'NA','country_code':'IN','user_id':'NA','cr_ifsc_code':'NA','pay§ee_name':'NA','error_code':'00','acct_o§pen_date':'01-01-2000','state_code':'','cust_id':'NA','p§os_entry_mode':'071','ip_country':'356','merchant_id':'ACQ§UIRER NAME','product_code':'N§A','cust_mob_no':'NA','par§§t_tran_type':'D','branch_id':'NA','sys_time':'07-10-2020 15:59:36.570','cu§st_name':'NA','tran_cde':'00','tran_date':'07-10-2020 15:59:36.570','tran_type':'NA','ip_address':'NA','§txn_secured_flag':'','host_id':'F','tran_particular':'POSEMV','cust_card_i§d':'4601340404000064','event_id':'ft_cardt§xn4852715855242963','atm_d§om_limit':'NA','ip_city':'CITY NAME','dr_acc§ount_id':'1351101033941','cr_account_id':'N§A'}\"}]";

    public static void main(String[] args) throws RunnerException, IOException {

        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        System.out.println(hardwareAbstractionLayer.getNetworkIFs().toString());

        //org.openjdk.jmh.Main.main(args);
    }

    public static String encrypt(String payload) {
        return encrypt(payload, StandardCharsets.UTF_8);
    }

    public static String encrypt(String payload, Charset charset) {
        int num = 10;

        int encryptedIndex = 0;
        byte[] bytes = payload.getBytes(charset);
        int length = bytes.length;
        byte[] encrypted = new byte[length * 3];
        for (byte aByte : bytes) {
            int asciiValue = aByte;
            num = ((num == 0) ? 10 : (num - 1));
            if (asciiValue <= LOW_WATER_MARK || asciiValue >= HIGH_WATER_MARK) {
                byte[] toAdd = String.valueOf(asciiValue + num).getBytes(charset);
                int addLength = toAdd.length;

                int modified = addLength + 2;
                byte[] limited = new byte[modified];
                limited[0] = (byte) 26;
                limited[1] = (byte) addLength;

                System.arraycopy(toAdd, 0, limited, 2, addLength);
                System.arraycopy(limited, 0, encrypted, encryptedIndex, modified);
                encryptedIndex += modified;
                continue;
            }

            asciiValue = asciiValue + num;
            asciiValue = (asciiValue > HIGH_WATER_MARK) ? (LOW_WATER_MARK + (asciiValue - HIGH_WATER_MARK)) : asciiValue;
            encrypted[encryptedIndex++] = (byte) asciiValue;
        }

        byte[] result = new byte[encryptedIndex];
        System.arraycopy(encrypted, 0, result, 0, encryptedIndex);
        return new String(result, charset);
    }

    public static String decrypt(String encryptedPayload) {
        return decrypt(encryptedPayload, StandardCharsets.UTF_8);
    }

    public static String decrypt(String encryptedPayload, Charset charset) {
        int num = 10;

        byte[] encrypted = encryptedPayload.getBytes(charset);
        byte[] decrypted = new byte[encrypted.length];
        int decryptedIndex = 0;
        for (int index = 0; index < encrypted.length; index++) {
            int asciiValue = encrypted[index];
            num = (num == 0) ? 10 : (num - 1);
            if (asciiValue == 26) {
                int modifiedLength = encrypted[++index];
                byte[] modified = new byte[modifiedLength];
                System.arraycopy(encrypted, index + 1, modified, 0, modifiedLength);
                decrypted[decryptedIndex++] = (byte) (Integer.parseInt(new String(modified, charset)) - num);
                index += modifiedLength;
                continue;
            }

            asciiValue = asciiValue - num;
            asciiValue = (asciiValue < LOW_WATER_MARK) ? (HIGH_WATER_MARK - (LOW_WATER_MARK - asciiValue)) : asciiValue;
            decrypted[decryptedIndex++] = (byte) asciiValue;
        }

        byte[] result = new byte[decryptedIndex];
        System.arraycopy(decrypted, 0, result, 0, decryptedIndex);
        return new String(result, charset);
    }
}