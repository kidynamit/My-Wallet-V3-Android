package info.blockchain.wallet;

import android.content.Context;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.crypto.MnemonicException;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;

import java.io.IOException;

import info.blockchain.wallet.access.AccessFactory;
import info.blockchain.wallet.crypto.AESUtil;
import info.blockchain.wallet.util.AppUtil;
import info.blockchain.wallet.util.CharSequenceX;
import info.blockchain.wallet.util.DoubleEncryptionFactory;
import info.blockchain.wallet.util.PrefsUtil;

public class BlockchainWalletTest extends BlockchainTest {

    /**
     * @param String name
     * @param Context ctx
     */
    public BlockchainWalletTest(String name, Context ctx) {
        super(name, ctx);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test something
     */
    public void test() {

        /*
         * In order to run these tests legit guid, shared key, pin identifiers, and encrypted passwords are needed.
         * The shared key can be obtained by observing the JSON console on your web wallet,
         * or by placing logcat statements in PinEntryActivity.java for this app and observing during an actual login.
         *
         * The pin identifier and the encrypted password can be obtained by observing AccessFactory.java for this app
         * during an actual login.
         */

        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_GUID, "70c46c4c-6fb2-4790-a4d9-9160ed942263");
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_SHARED_KEY, "da34c5df854679ba24201cefa4d87e92");

        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_PIN_IDENTIFIER, "eca19737359f4df13fa19e88fb34b36c");
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_ENCRYPTED_PASSWORD, "opgdX074+w2yp1aRnK5XfVauaPFbjP7HWdp7eTFE8U0ErWZp/8kzdcqaBCR2re9y");

        CharSequenceX pw = new CharSequenceX("blockchain_test_wallet_1");

        loginGoodParams(pw);

        loginBadPW(new CharSequenceX("bogus"));

        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_GUID, "12345678-9abc-def0-ffff-ffffffffffff");
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_SHARED_KEY, "12345678-9abc-def0-ffff-ffffffffffff");

        loginBadParams(pw);

        //
        // login w/ PIN tests
        //
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_GUID, "70c46c4c-6fb2-4790-a4d9-9160ed942263");
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_SHARED_KEY, "da34c5df854679ba24201cefa4d87e92");

        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_PIN_IDENTIFIER, "eca19737359f4df13fa19e88fb34b36c");
        PrefsUtil.getInstance(context).setValue(PrefsUtil.KEY_ENCRYPTED_PASSWORD, "opgdX074+w2yp1aRnK5XfVauaPFbjP7HWdp7eTFE8U0ErWZp/8kzdcqaBCR2re9y");

        loginGoodPIN();

        loginBadPIN();

    }

    public void loginGoodParams(CharSequenceX pw) {
        //
        // login w/ password tests
        //
        boolean loggedIn = false;

        try {
            loggedIn = HDPayloadBridge.getInstance(context).init(pw);
        }
        catch(IOException | DecoderException | AddressFormatException
                | MnemonicException.MnemonicLengthException | MnemonicException.MnemonicChecksumException
                | MnemonicException.MnemonicWordException | JSONException e) {
            e.printStackTrace();
        }
        finally {
            AssertUtil.getInstance().assert_true(this, "Logged in with proper credentials", loggedIn);
        }
    }

    public void loginBadPW(CharSequenceX pw) {

        boolean loggedIn = false;

        try {
            loggedIn = HDPayloadBridge.getInstance(context).init(new CharSequenceX(pw));
        }
        catch(IOException | DecoderException | AddressFormatException
                | MnemonicException.MnemonicLengthException | MnemonicException.MnemonicChecksumException
                | MnemonicException.MnemonicWordException | JSONException e) {
            ;
        }
        finally {
            AssertUtil.getInstance().assert_true(this, "Not logged in with bad password", !loggedIn);
        }
    }

    public void loginBadParams(CharSequenceX pw) {

        boolean loggedIn = false;

        try {
            loggedIn = HDPayloadBridge.getInstance(context).init(pw);
        }
        catch(IOException | DecoderException | AddressFormatException
                | MnemonicException.MnemonicLengthException | MnemonicException.MnemonicChecksumException
                | MnemonicException.MnemonicWordException | JSONException e) {
            ;
        }
        finally {
            AssertUtil.getInstance().assert_true(this, "Not logged in with bad credentials", !loggedIn);
        }
    }

    public void loginGoodPIN() {
        CharSequenceX password = AccessFactory.getInstance(context).validatePIN("3704");
        AssertUtil.getInstance().assert_true(this, "Logged in with good PIN", password != null);
    }

    public void loginBadPIN() {
        CharSequenceX password = AccessFactory.getInstance(context).validatePIN("9999");
        AssertUtil.getInstance().assert_true(this, "Not logged in with bad PIN", password == null);
    }

}
