import java.util.ArrayList;

public class TxHandler {

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	private UTXOPool myPool;

	public TxHandler(UTXOPool utxoPool) {
		// create defensive copy of utxoPool
		myPool = new UTXOPool(utxoPool);

		for (UTXO tx : myPool.getAllUTXO()) {
			System.out.println("TxHandler constructor: " + tx.toString());
			// is output claimed by tx in the current UTXO pool?
		}
	}

	/**
	 * @return true if: (1) all outputs claimed by {@code tx} are in the current
	 *         UTXO pool, (2) the signatures on each input of {@code tx} are
	 *         valid, (3) no UTXO is claimed multiple times by {@code tx}, (4)
	 *         all of {@code tx}s output values are non-negative, and (5) the
	 *         sum of {@code tx}s input values is greater than or equal to the
	 *         sum of its output values; and false otherwise.
	 */
	public boolean isValidTx(Transaction tx) {
		/**
		 * check all outputs (from a prev tx) claimed by this tx
		 */
		// System.out.println("hello, isValidTx");

		UTXO utxo;
		// remember each validated UTXO in order to detect duplicate claims
		ArrayList<UTXO> validatedUTXOs = new ArrayList<UTXO>();
		double inputSum = 0.00, outputSum = 0.00;
//		double txoVal;

		for (Transaction.Input input : tx.getInputs()) {
			int inputIndex = tx.getInputs().indexOf(input);
			
			utxo = new UTXO(input.prevTxHash, input.outputIndex);

			// verify this UTXO not already in the list of remembered UTXOs
			if (validatedUTXOs.contains(utxo)) {
				return false;
			} else {
				validatedUTXOs.add(utxo);
			}

			// verify the claimed prev tx's output is in the unspent pool
			if (!myPool.contains(utxo)) {
				return false;
			}

			// verify the signature of that prev tx's output
			Transaction.Output txo = myPool.getTxOutput(utxo);
			if (!Crypto.verifySignature(txo.address, tx.getRawDataToSign(inputIndex), input.signature)) {
				return false;
			}

//			// verify output value is non-negative
			inputSum += txo.value;
//			if (Double.compare(txoVal, 0.00) < 0) {
//				return false;
//			}
			
		}

		// verify that all output values are non-negative
		for (Transaction.Output output : tx.getOutputs()) {
			double txValue = output.value;
			if (Double.compare(txValue, 0.00) < 0) {
				return false;
			}
		}
		
		// verify that sum of inputs >= sum of outputs
		for (Transaction.Output output : tx.getOutputs()) {
			outputSum += output.value;
		}
		if (Double.compare(inputSum, outputSum) < 0) {
			return false;
		}

		return true;
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed
	 * transactions, checking each transaction for correctness, returning a
	 * mutually valid array of accepted transactions, and updating the current
	 * UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		Transaction[] validTxs;
		ArrayList<Transaction> validatedTxs = new ArrayList<>();
		UTXO utxo = null;
//		int idx = 0; // counter for index values
		
		// do a first pass to process tx's that are independently valid
		for (Transaction tx : possibleTxs) {
			
			if (isValidTx(tx)) {
				
				for (Transaction.Input input : tx.getInputs()) {
					utxo = new UTXO(input.prevTxHash, input.outputIndex);
					myPool.removeUTXO(utxo);
				}
				
				for (int idx = 0; idx < tx.numOutputs(); idx++) {
					myPool.addUTXO(new UTXO(tx.getHash(), idx), tx.getOutput(idx));
				}
			}
			validatedTxs.add(tx);
		}
		
		// second pass to find tx's that were valid but depended on others in the same block
		for (Transaction tx : possibleTxs) {
			
			// don't re-process tx's that were already independently valid
			if (!validatedTxs.contains(tx)) {
				continue;
			}
			
			if (isValidTx(tx)) {
				for (Transaction.Input input : tx.getInputs()) {
					utxo = new UTXO(input.prevTxHash, input.outputIndex);
					myPool.removeUTXO(utxo);
				}
				
				for (int idx = 0; idx < tx.numOutputs(); idx++) {
					myPool.addUTXO(new UTXO(tx.getHash(), idx), tx.getOutput(idx));
				}
			}
			validatedTxs.add(tx);
		}
		
		// convert ArrayList to the return Transactions array
		validTxs = new Transaction[validatedTxs.size()];
		validatedTxs.toArray(validTxs);
		
		return possibleTxs;
	}

}
