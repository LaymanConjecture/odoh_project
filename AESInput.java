/*******************************************************************************
 * Author: Ahmed Kosba <akosba@cs.umd.edu>
 *******************************************************************************/

package examples.generators.blockciphers;

import java.math.BigInteger;
import java.util.Arrays;

import circuit.config.Config;
import circuit.eval.CircuitEvaluator;
import circuit.structure.CircuitGenerator;
import circuit.structure.Wire;
import examples.gadgets.blockciphers.AES128CipherGadget;

import java.util.Scanner;


// A sample usage of the AES128 block cipher gadget
public class AESInput extends CircuitGenerator {

	private Wire[] inputs;
	private Wire[] key;
	private Wire[] outputs;
	private AES128CipherGadget gadget;
    private String inputKeyV;
    private String inputMsgV;

	public AESInput(String circuitName) {
		super(circuitName);
	}

	@Override
	protected void buildCircuit() {
		inputs = createInputWireArray(16); // in bytes
		key = createInputWireArray(16); // in bytes

		Wire[] expandedKey = AES128CipherGadget.expandKey(key);
		gadget = new AES128CipherGadget(inputs, expandedKey, "");
		outputs = gadget.getOutputWires();
		for (Wire o : outputs) {
			makeOutput(o);
		}

	}

	@Override
	public void generateSampleInput(CircuitEvaluator circuitEvaluator) {

		//BigInteger keyV = new BigInteger("2b7e151628aed2a6abf7158809cf4f3c", 16);
		//BigInteger msgV = new BigInteger("ae2d8a571e03ac9c9eb76fac45af8e51", 16);

		// expected output:0xf5d3d58503b9699de785895a96fdbaaf
        BigInteger keyV = new BigInteger(inputKeyV, 16);
        BigInteger msgV = new BigInteger(inputMsgV, 16);

		byte[] keyArray = keyV.toByteArray();
		byte[] msgArray = msgV.toByteArray();
		msgArray = Arrays.copyOfRange(msgArray, msgArray.length - 16,
				msgArray.length);
		keyArray = Arrays.copyOfRange(keyArray, keyArray.length - 16,
				keyArray.length);

		for (int i = 0; i < msgArray.length; i++) {
			circuitEvaluator.setWireValue(inputs[i], (msgArray[i] & 0xff));
		}

		for (int i = 0; i < keyArray.length; i++) {
			circuitEvaluator.setWireValue(key[i], (keyArray[i] & 0xff));
		}
	}

	public static void main(String[] args) throws Exception {
        System.out.print("Enter inputKeyV : ");
        Scanner scanner = new Scanner(System.in);
        inputKeyV = scanner.nextLine();
        System.out.print("Enter inputMsgV : ");
        Scanner scanner = new Scanner(System.in);
        inputMsgV = scanner. nextLine();

		Config.hexOutputEnabled = true;
		AESInput generator = new AESInput(
				"AES_Circuit");
		generator.generateCircuit();
		generator.evalCircuit();
		generator.prepFiles();
		generator.runLibsnark();

	}
}
