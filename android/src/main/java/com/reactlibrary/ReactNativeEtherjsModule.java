// ReactNativeEtherjsModule.java

package com.reactlibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeMap;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip44WalletUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;

public class ReactNativeEtherjsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public ReactNativeEtherjsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "ReactNativeEtherjs";
    }

    @ReactMethod
    public void fromMnemonic(String mnemonic, Promise promise) {
        String password = null; // no encryption
        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

        Bip32ECKeyPair bip44Keypair = Bip44WalletUtils.generateBip44KeyPair(masterKeypair);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(bip44Keypair);

        WritableNativeMap wallet = new WritableNativeMap();
        wallet.putString("address",credentials.getAddress());
        wallet.putString("publicKey",credentials.getEcKeyPair().getPublicKey().toString());
        wallet.putString("privateKey",credentials.getEcKeyPair().getPrivateKey().toString());
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("success",true);
        map.putMap("wallet",wallet);
        promise.resolve(map);
    }

}
