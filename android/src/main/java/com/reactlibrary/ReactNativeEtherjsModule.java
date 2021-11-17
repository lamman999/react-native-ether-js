// ReactNativeEtherjsModule.java

package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeMap;

import org.web3j.crypto.Bip32ECKeyPair;
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
    public void fromMnemonic(String mnemonic, Callback callback) {
        String password = null; // no encryption
        //Derivation path wanted: // m/44'/60'/0'/0
        int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};

        // Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

        // Derived the key using the derivation path
        Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

        // Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);

        WritableNativeMap wallet = new WritableNativeMap();
        wallet.putString("address",credentials.getAddress());
        wallet.putString("publicKey",credentials.getEcKeyPair().getPublicKey().toString());
        wallet.putString("privateKey",credentials.getEcKeyPair().getPrivateKey().toString());
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("success",true);
        map.putMap("wallet",wallet);
        callback.invoke(map);
    }

}
