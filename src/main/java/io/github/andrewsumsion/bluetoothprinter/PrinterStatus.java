package io.github.andrewsumsion.bluetoothprinter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PrinterStatus {
    public boolean coverOpen;
    public boolean offline;
    public boolean compulsionSwitch;
    public boolean overTemp;
    public boolean unrecoverableError;
    public boolean cutterError;
    public boolean mechError;
    public boolean headThermistorError;
    public boolean receiveBufferOverflow;
    public boolean pageModeCmdError;
    public boolean paperDetectionError;
    public boolean blackMarkError;
    public boolean jamError;
    public boolean presenterPaperJamError;
    public boolean headUpError;
    public boolean voltageError;
    public boolean receiptBlackMarkDetection;
    public boolean receiptPaperEmpty;
    public boolean receiptPaperNearEmptyInner;
    public boolean receiptPaperNearEmptyOuter;
    public boolean paperPresent;
    public boolean presenterPaperPresent;
    public boolean peelerPaperPresent;
    public boolean stackerFull;
    public boolean slipTOF;
    public boolean slipCOF;
    public boolean slipBOF;
    public boolean validationPaperPresent;
    public boolean slipPaperPresent;
    public boolean etbAvailable;
    public int etbCounter;
    public int presenterState;
    public int connectedInterface;
    public int rawLength;

    public boolean etbExecuted;

    public PrinterStatus() {
        
    }

    public byte[] toRawData() {
        int rawLength = 9;
        if(connectedInterface != 0) {
            rawLength = 12;
        }

        byte[] raw = new byte[rawLength];

        if(rawLength == 7){
            raw[0] = 0b00001111;
        } else {
            raw[0] = (byte) (0b00100001 | ((rawLength & 0b00000111) << 1));
        }

        raw[1] = 6;

        raw[2] = (byte) (raw[2] | (coverOpen                  ? 32 : 0));
        raw[2] = (byte) (raw[2] | (offline                    ? 8  : 0));
        raw[2] = (byte) (raw[2] | (compulsionSwitch           ? 4  : 0));
        raw[2] = (byte) (raw[2] | (etbExecuted                ? 2  : 0));
        raw[3] = (byte) (raw[3] | (overTemp                   ? 64 : 0));
        raw[3] = (byte) (raw[3] | (unrecoverableError         ? 32 : 0));
        raw[3] = (byte) (raw[3] | (cutterError                ? 8  : 0));
        raw[3] = (byte) (raw[3] | (mechError                  ? 4  : 0));
        raw[3] = (byte) (raw[3] | (headThermistorError        ? 4  : 0));
        raw[4] = (byte) (raw[4] | (receiveBufferOverflow      ? 64 : 0));
        raw[4] = (byte) (raw[4] | (pageModeCmdError           ? 32 : 0));
        raw[4] = (byte) (raw[4] | (paperDetectionError        ? 8  : 0));
        raw[4] = (byte) (raw[4] | (blackMarkError             ? 8  : 0));
        raw[4] = (byte) (raw[4] | (jamError                   ? 4  : 0));
        raw[4] = (byte) (raw[4] | (presenterPaperJamError     ? 4  : 0));
        raw[4] = (byte) (raw[4] | (headUpError                ? 2  : 0));
        raw[4] = (byte) (raw[4] | (voltageError               ? 2  : 0));
        raw[5] = (byte) (raw[5] | (receiptBlackMarkDetection  ? 32 : 0));
        raw[5] = (byte) (raw[5] | (receiptPaperEmpty          ? 8  : 0));
        raw[5] = (byte) (raw[5] | (receiptPaperNearEmptyInner ? 4  : 0));
        raw[5] = (byte) (raw[5] | (receiptPaperNearEmptyOuter ? 2  : 0));
        raw[6] = (byte) (raw[6] | (paperPresent               ? 2  : 0));
        //raw[6] = (byte) (raw[6] | (presenterPaperPresent      ? 2  : 0));
        //raw[6] = (byte) (raw[6] | (peelerPaperPresent         ? 2  : 0));
        //raw[6] = (byte) (raw[6] | (stackerFull                ? 2  : 0));
        //raw[6] = (byte) (raw[6] | (!slipTOF                   ? 2  : 0));
        raw[6] = (byte) (raw[6] | (!slipCOF                   ? 4  : 0));
        raw[6] = (byte) (raw[6] | (!slipBOF                   ? 8  : 0));

        raw[6] = (byte) (raw[6] | 32 | 64);

        if(slipPaperPresent && !validationPaperPresent) {
            raw[6] = (byte) (raw[6] & ~64);
        }
        else if(!slipPaperPresent && validationPaperPresent) {
            raw[6] = (byte) (raw[6] & ~32);
        }

        // raw[7].64bit = etbCounter.16bit
        // raw[7].32bit = etbCounter.8bit
        // raw[7].8bit  = etbCounter.4bit
        // raw[7].4bit  = etbCounter.2bit
        // raw[7].2bit  = etbCounter.1bit

        raw[7] = (byte) (raw[7] | ((etbCounter & 16) << 2));
        raw[7] = (byte) (raw[7] | ((etbCounter & 8)  << 2));
        raw[7] = (byte) (raw[7] | ((etbCounter & 4)  << 1));
        raw[7] = (byte) (raw[7] | ((etbCounter & 2)  << 1));
        raw[7] = (byte) (raw[7] | ((etbCounter & 1)  << 1));

        if(etbAvailable) {
            raw[8] = (byte) ((presenterState & 0b0000_0111) << 1);
        }

        if(connectedInterface != 0){
            raw[11] = (byte) ((connectedInterface & 0b0000_0111) << 1);
        }

        if(rawLength == 12) {
            raw[0] = (byte) 41;
        }

        return raw;
    }

    public void fromRawData(byte[] raw) { // TODO: REVERSE ENGINEER THIS
        coverOpen = ((int)raw[2] & 32) != 0;
        offline = ((int)raw[2] & 8) != 0;
        compulsionSwitch = ((int)raw[2] & 4) != 0;
        overTemp = ((int)raw[3] & 64) != 0;
        unrecoverableError = ((int)raw[3] & 32) != 0;
        cutterError = ((int)raw[3] & 8) != 0;
        mechError = ((int)raw[3] & 4) != 0;
        headThermistorError = ((int)raw[3] & 4) != 0;
        receiveBufferOverflow = ((int)raw[4] & 64) != 0;
        pageModeCmdError = ((int)raw[4] & 32) != 0;
        paperDetectionError = ((int)raw[4] & 8) != 0;
        blackMarkError = ((int)raw[4] & 8) != 0;
        jamError = ((int)raw[4] & 4) != 0;
        presenterPaperJamError = ((int)raw[4] & 4) != 0;
        headUpError = ((int)raw[4] & 2) != 0;
        voltageError = ((int)raw[4] & 2) != 0;
        receiptBlackMarkDetection = ((int)raw[5] & 32) != 0;
        receiptPaperEmpty = ((int)raw[5] & 8) != 0;
        receiptPaperNearEmptyInner = ((int)raw[5] & 4) != 0;
        receiptPaperNearEmptyOuter = ((int)raw[5] & 2) != 0;
        paperPresent = ((int)raw[6] & 2) != 0;
        presenterPaperPresent = ((int)raw[6] & 2) != 0;
        peelerPaperPresent = ((int)raw[6] & 2) != 0;
        stackerFull = ((int)raw[6] & 2) != 0;
        slipTOF = ((int)raw[6] & 2) == 0;
        slipCOF = ((int)raw[6] & 4) == 0;
        slipBOF = ((int)raw[6] & 8) == 0;
        int i = (int) raw[6] & 64;
        label1: {
            label2: {
                label3: {
                    label4: {
                        if (i != 0) {
                            break label4;
                        }
                        if (((int) raw[6] & 32) == 0) {
                            break label3;
                        }
                    }
                    if (((int) raw[6] & 64) != 0) {
                        break label2;
                    }
                    if (((int) raw[6] & 32) == 0) {
                        break label2;
                    }
                }
                slipPaperPresent = true;
                validationPaperPresent = false;
                break label1;
            }
            int i0 = (int) raw[6] & 64;
            label0: {
                if (i0 == 0) {
                    break label0;
                }
                if (((int) raw[6] & 32) != 0) {
                    break label0;
                }
                slipPaperPresent = false;
                validationPaperPresent = true;
                break label1;
            }
            if (((int) raw[6] & 64) != 0 && ((int) raw[6] & 32) != 0) {
                slipPaperPresent = false;
                validationPaperPresent = false;
            }
        }
        etbAvailable = rawLength >= 9;
        etbCounter = ((int) raw[7] & 64) >> 2 | ((int) raw[7] & 32) >> 2 | ((int) raw[7] & 8) >> 1 | ((int) raw[7] & 4) >> 1 | ((int) raw[7] & 2) >> 1;
        presenterState = ((int) raw[8] & 8) >> 1 | ((int) raw[8] & 4) >> 1 | ((int) raw[8] & 2) >> 1;
        if (rawLength < 12) {
            connectedInterface = 0;
        } else {
            connectedInterface = ((int) raw[11] & 14) >> 1;
        }
    }

    public static void main(String[] args) throws IllegalAccessException {
        HashMap<String, String> result = new HashMap<String, String>();
        String string = "FakeBluetoothPrinterVer1.0.0";
        int i = string.indexOf("Ver");
        result.put("MODEL_NAME", string.substring(0, i));
        result.put("FW_VERSION", string.substring(i + 3));
        for(Map.Entry<String, String> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
