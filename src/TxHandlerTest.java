import static org.junit.Assert.*;

import java.security.PublicKey;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

/**
 * @author Stephen
 *
 */
public class TxHandlerTest {

	private static UTXOPool testUtxoPool;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// create 5 unspent tx outputs and add to pool
		testUtxoPool = new UTXOPool(); // create pool first

		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);
		UTXO utxo0 = new UTXO(txHash0, 0);
		testUtxoPool.addUTXO(utxo0, null);

		byte[] txHash1 = new byte[256];
		java.util.Arrays.fill(txHash1, (byte) 1);
		UTXO utxo1 = new UTXO(txHash1, 1);
		testUtxoPool.addUTXO(utxo1, null);

		byte[] txHash2 = new byte[256];
		java.util.Arrays.fill(txHash2, (byte) 2);
		UTXO utxo2 = new UTXO(txHash2, 2);
		testUtxoPool.addUTXO(utxo2, null);

		byte[] txHash3 = new byte[256];
		java.util.Arrays.fill(txHash3, (byte) 3);
		UTXO utxo3 = new UTXO(txHash3, 3);
		testUtxoPool.addUTXO(utxo3, null);

		// byte[] txHash4 = new byte[256];
		// java.util.Arrays.fill(txHash4, (byte)4);
		// UTXO utxo4 = new UTXO(txHash4, 4);
		// testUtxoPool.addUTXO(utxo4, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link TxHandler#TxHandler(UTXOPool)}.
	 */
	@Test
	public final void testTxHandler() {
		assertTrue("We already test constructor in other unit tests", true);
	}

	/**
	 * Test method for {@link TxHandler#isValidTx(Transaction)}.
	 */
	@Test
	public final void testIsValidTx_oneVal_oneInPool() {
		// attach a txhash & serial nbr (i.e. the claimed output of a previous
		// tx) to the input of a new tx
		Transaction currTx = new Transaction();

		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);

		currTx.addInput(txHash0, 0);
		
		// create my own test unspent tx pool
		UTXO utxo0 = new UTXO(txHash0, 0);

		UTXOPool utxoPool = new UTXOPool();
		utxoPool.addUTXO(utxo0, null);

		// now check this input is inside the test utxopool (which it should be
		// of course)
		TxHandler txhandler = new TxHandler(utxoPool);
		
		assertTrue(txhandler.isValidTx(currTx));
	}

	/**
	 * Test method for {@link TxHandler#isValidTx(Transaction)}.
	 */
	@Test
	public final void testIsValidTx_oneVal_3inPool() {
		// create 3 unspent tx outputs and add to pool
		UTXOPool utxoPool = new UTXOPool(); // create pool first

		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);
		UTXO utxo0 = new UTXO(txHash0, 0);
		utxoPool.addUTXO(utxo0, null);

		byte[] txHash1 = new byte[256];
		java.util.Arrays.fill(txHash1, (byte) 1);
		UTXO utxo1 = new UTXO(txHash1, 1);
		utxoPool.addUTXO(utxo1, null);

		byte[] txHash2 = new byte[256];
		java.util.Arrays.fill(txHash2, (byte) 2);
		UTXO utxo2 = new UTXO(txHash2, 2);
		utxoPool.addUTXO(utxo2, null);

		// create a new tx and attach an input corresponding to one of the UTXOs
		Transaction currTx = new Transaction();
		currTx.addInput(txHash1, 1);

		// now check this input is inside the test utxopool (which it should be
		// of course)
		TxHandler txhandler = new TxHandler(utxoPool);

//		ArrayList<UTXO> uList = new ArrayList<UTXO>();
//		assertFalse(uList.contains(utxo1));
//		uList.add(utxo0);
//		uList.add(utxo2);
//		UTXO tmp = new UTXO(txHash2, 2);
//		assertTrue(uList.contains(tmp));
		
		assertTrue(txhandler.isValidTx(currTx));
	}

	/**
	 * Test method for {@link TxHandler#isValidTx(Transaction)}.
	 */
	@Test
	public final void testIsValidTx_oneInval() {
		// add UTXO0 to pool
		// create tx and add txHash1/serial1 to input
		Transaction currTx = new Transaction();

		// create tx field values
		byte[] txHash0 = new byte[256]; // generate dummy hash
		java.util.Arrays.fill(txHash0, (byte) 0);
		UTXO utxo1 = new UTXO(txHash0, 1); // add hash and sequence # to new
											// unspent tx

		UTXOPool utxoPool = new UTXOPool();
		utxoPool.addUTXO(utxo1, null);

		byte[] txHash1 = new byte[256];
		java.util.Arrays.fill(txHash1, (byte) 1);
		currTx.addInput(txHash1, 0);

		// now check a different tx in input is inside the test utxopool
		TxHandler txhandler = new TxHandler(utxoPool);
		assertFalse(txhandler.isValidTx(currTx));
	}

	/**
	 * Test method for {@link TxHandler#isValidTx(Transaction)}.
	 */
	@Test
	public final void testIsValidTx_invalSerialNbr() {
		// add the same hash to the tx input and to the pool but use diff serial
		// numbers
		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);
		UTXO utxo0 = new UTXO(txHash0, 0); // serial nbr 0
		UTXOPool utxoPool = new UTXOPool();
		utxoPool.addUTXO(utxo0, null);

		Transaction currTx = new Transaction();
		currTx.addInput(txHash0, 1); // add to tx with serial nbr 1

		// now check that diff serial nbrs validates to false
		TxHandler txhandler = new TxHandler(utxoPool);
		assertFalse(txhandler.isValidTx(currTx));
	}

	/**
	 * Test method for {@link TxHandler#isValidTx(Transaction)}.
	 * Negative test where output value is negative - should be invalid
	 */
	@Test
	public final void testIsValidTx_OutputValNegative() {
		
	}
	
	/**
	 * Test method for {@link TxHandler#handleTxs(Transaction[])}. Straight
	 * positive test (tx #1 with 1 valid UTXO, tx #2 with 3 valid UTXOs)
	 */
	@Test
	public final void testHandleTxs_UtxosExactlyMatchPool() {
		// use the test classes TxHandler's pool for testing
		// give it a UTXOPool for testing
		TxHandler txhandler = new TxHandler(testUtxoPool);

		// create outputs that I already know are in the pool
		// hash for utxo0
		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);

		// hash for utxo1
		byte[] txHash1 = new byte[256];
		java.util.Arrays.fill(txHash1, (byte) 1);

		// hash for utxo2
		byte[] txHash2 = new byte[256];
		java.util.Arrays.fill(txHash2, (byte) 2);

		// hash for utxo3
		byte[] txHash3 = new byte[256];
		java.util.Arrays.fill(txHash3, (byte) 3);

		// add these hashes and sequence #'s to transactions
		// add hash corresponding to utxo0 to tx0
		Transaction tx0 = new Transaction();
		tx0.addInput(txHash0, 0);

		// add hashes corresp'ing to utxo1, utxo2, and utxo3, respectively, to
		// tx1
		Transaction tx1 = new Transaction();
		tx1.addInput(txHash1, 1);
		tx1.addInput(txHash2, 2);
		tx1.addInput(txHash3, 3);

		// add both transactions to array and send to TxHandler
		Transaction[] possibleTxs = { tx0, tx1 };
		Transaction[] returnedTxs = txhandler.handleTxs(possibleTxs);
		assertArrayEquals(possibleTxs, returnedTxs);
	}

	/**
	 * Test method for {@link TxHandler#handleTxs(Transaction[])}. Straight
	 * positive test (tx #1 with 1 valid UTXO, tx #2 with 2 valid UTXOs)
	 */
	@Test
	public final void testHandleTxs_UtxosSubsetOfPool() {
		// use the test classes TxHandler's pool for testing
		// give it a UTXOPool for testing
		TxHandler txhandler = new TxHandler(testUtxoPool);

		// create outputs that I already know are in the pool
		// hash for utxo0
		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);

		// hash for utxo1
		byte[] txHash1 = new byte[256];
		java.util.Arrays.fill(txHash1, (byte) 1);

		// hash for utxo2
		byte[] txHash2 = new byte[256];
		java.util.Arrays.fill(txHash2, (byte) 2);

		// add these hashes and sequence #'s to transactions
		// add hash corresponding to utxo0 to tx0
		Transaction tx0 = new Transaction();
		tx0.addInput(txHash0, 0);

		// add hashes corresp'ing to utxo1 and utxo2, respectively, to tx1
		Transaction tx1 = new Transaction();
		tx1.addInput(txHash1, 1);
		tx1.addInput(txHash2, 2);

		// add both transactions to array and send to TxHandler
		Transaction[] possibleTxs = { tx0, tx1 };
		Transaction[] returnedTxs = txhandler.handleTxs(possibleTxs);
		assertArrayEquals(possibleTxs, returnedTxs);
	}

	/**
	 * Test method for {@link TxHandler#handleTxs(Transaction[])}. One claimed
	 * UTXO not in the pool (tx #1 with 1 valid UTXO, tx #2 with 1 valid & 1
	 * invalid UTXO)
	 */
	@Test
	public final void testHandleTxs_UtxoNotInPool() {
		// use the test classes TxHandler's pool for testing
		// give it a UTXOPool for testing
		TxHandler txhandler = new TxHandler(testUtxoPool);

		// create outputs that I already know are in the pool
		// hash for utxo0
		byte[] txHash0 = new byte[256];
		java.util.Arrays.fill(txHash0, (byte) 0);

		// hash for utxo1
		byte[] txHash4 = new byte[256];
		java.util.Arrays.fill(txHash4, (byte) 4);

		// hash for utxo4 -- this is NOT an unspent transaction output in the
		// pool
		byte[] txHash2 = new byte[256];
		java.util.Arrays.fill(txHash2, (byte) 2);

		// add these hashes and sequence #'s to transactions
		// add hash corresponding to utxo0 to tx0
		Transaction tx0 = new Transaction();
		tx0.addInput(txHash0, 0);

		// add hashes corresp'ing to utxo1 and spent/invalid utxo to tx1
		Transaction tx1 = new Transaction();
		tx1.addInput(txHash4, 1);
		tx1.addInput(txHash2, 2);

		// add both transactions to array and send to TxHandler
		Transaction[] possibleTxs = { tx0, tx1 };
		Transaction[] returnedTxs = txhandler.handleTxs(possibleTxs);

		assertArrayEquals(returnedTxs, new Transaction[] { tx0 });
	}

}
