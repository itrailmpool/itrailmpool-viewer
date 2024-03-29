/*
 * BitcoindRpcClient-JSON-RPC-Client License
 * 
 * Copyright (c) 2013, Mikhail Yevchenko.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
 /*
 * Repackaged with simple additions for easier maven usage by Alessandro Polverini
 */
package com.itrailmpool.itrailmpoolviewer.client.rpc.javabitcoindrpcclient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mikhail Yevchenko m.ṥῥẚɱ.ѓѐḿởύḙ@azazar.com Small modifications by
 * Alessandro Polverini polverini at gmail.com
 */
public interface BitcoindRpcClient {

  /* Missing methods:
   getblocktemplate ( "jsonrequestobject" )
   *getgenerate
   *gethashespersec
   *getwork ( "data" )
   help ( "command" )
   *listaddressgroupings
   *listlockunspent
   (DEPRECATED) listreceivedbyaccount ( minconf includeempty )
   lockunspent unlock [{"txid":"txid","vout":n},...]
   sendmany "fromaccount" {"address":amount,...} ( minconf "comment" )
   (DEPRECATED) setaccount "bitcoinaddress" "account"
   */
  public static interface TxInput extends Serializable {

    public String txid();

    public int vout();

    public String scriptPubKey();
  }

  @SuppressWarnings("serial")
  public static class BasicTxInput implements TxInput {

    public String txid;
    public int vout;
    public String scriptPubKey;

    public BasicTxInput(String txid, int vout) {
      this.txid = txid;
      this.vout = vout;
    }

    public BasicTxInput(String txid, int vout, String scriptPubKey) {
      this(txid, vout);
      this.scriptPubKey = scriptPubKey;
    }

    @Override
    public String txid() {
      return txid;
    }

    @Override
    public int vout() {
      return vout;
    }

    @Override
    public String scriptPubKey() {
      return scriptPubKey;
    }

  }

  @SuppressWarnings("serial")
  public static class ExtendedTxInput extends BasicTxInput {

    public String redeemScript;
    public BigDecimal amount;

    public ExtendedTxInput(String txid, int vout) {
      super(txid, vout);
    }

    public ExtendedTxInput(String txid, int vout, String scriptPubKey) {
      super(txid, vout, scriptPubKey);
    }

    public ExtendedTxInput(String txid, int vout, String scriptPubKey, String redeemScript, BigDecimal amount) {
      super(txid, vout, scriptPubKey);
      this.redeemScript = redeemScript;
      this.amount = amount;
    }

    public String redeemScript() {
      return redeemScript;
    }

    public BigDecimal amount() {
      return amount;
    }

  }

  public static interface TxOutput extends Serializable {

    public String address();

    public double amount();
  }

  @SuppressWarnings("serial")
  public static class BasicTxOutput implements TxOutput {

    public String address;
    public double amount;

    public BasicTxOutput(String address, double amount) {
      this.address = address;
      this.amount = amount;
    }

    @Override
    public String address() {
      return address;
    }

    @Override
    public double amount() {
      return amount;
    }

    public void setAmount(double amount) {
      this.amount = amount;
    }

    public double getAmount() {
      return amount;
    }


  }

  public String getURL();

  /**
   * The createrawtransaction RPC creates an unsigned serialized transaction that spends a previous output to a new output with a P2PKH or P2SH address.
   * The transaction is not stored in the wallet or transmitted to the network.
   *
   * @param inputs An array of objects, each one to be used as an input to the transaction
   * @param outputs The addresses and amounts to pay
   * @return The resulting unsigned raw transaction in serialized transaction format encoded as hex.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#createrawtransaction">createrawtransaction</a>
   */
  String createRawTransaction(List<TxInput> inputs, List<TxOutput> outputs) throws GenericRpcException;


  /**
   * The createrawtransaction RPC creates an unsigned serialized transaction that spends a previous output to a new output with a P2PKH or P2SH address.
   * The transaction is not stored in the wallet or transmitted to the network.
   *
   * @param inputs An array of objects, each one to be used as an input to the transaction
   * @param outputs The addresses and amounts to pay
   * @param locktime Raw locktime. Non-0 value also locktime-activates inputs
   * @param replaceable Marks this transaction as BIP125-replaceable.
   * @return The resulting unsigned raw transaction in serialized transaction format encoded as hex.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#createrawtransaction">createrawtransaction</a>
   */
  String createRawTransaction(List<TxInput> inputs, List<TxOutput> outputs, int locktime, boolean replaceable) throws GenericRpcException;

  /**
   * The dumpprivkey RPC returns the wallet-import-format (WIF) private key corresponding to an address.
   * (But does not remove it from the wallet.)
   *
   * @param address The P2PKH address corresponding to the private key you want returned.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#dumpprivkey">dumpprivkey</a>
   */
  public String dumpPrivKey(String address) throws GenericRpcException;

  /**
   * The getaccount RPC returns the name of the account associated with the given address.
   *
   * @param address A P2PKH or P2SH Bitcoin address belonging either to a specific account or the default account
   * @return The name of an account, or an empty string
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getaccount">getaccount</a>
   */
  public String getAccount(String address) throws GenericRpcException;

  /**
   * The getaccountaddress RPC returns the current Bitcoin address for receiving payments to this account.
   * If the account doesn’t exist, it creates both the account and a new address for receiving payment.
   * Once a payment has been received to an address, future calls to this RPC for the same account will return a different address.
   *
   * @param account The name of an account.
   * @return An address, belonging to the account specified, which has not yet received any payments
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getaccountaddress">getaccountaddress</a>
   */
  public String getAccountAddress(String account) throws GenericRpcException;

  /**
   * The getaddressesbyaccount RPC returns a list of every address assigned to a particular account.
   *
   * @param account The name of an account to get the balance for.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getaddressesbyaccount">getaddressesbyaccount</a>
   */
  public List<String> getAddressesByAccount(String account) throws GenericRpcException;

  /**
   * The getaddressesbylabel RPC returns a list of every address assigned to a particular label.
   *
   * @param label The name of an label.
   *
   * @see <a href="https://bitcoincore.org/en/doc/0.18.0/rpc/wallet/getaddressesbylabel/">getaddressesbylabel</a>
   */
  public List<String> getAddressesByLabel(String label) throws GenericRpcException;

  /**
   * The getbalance RPC gets the balance in decimal bitcoins for the default account.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getbalance">getbalance</a>
   */
  public double getBalance() throws GenericRpcException;

  /**
   * The getbalance RPC gets the balance in decimal bitcoins across all accounts or for a particular account.
   *
   * @param account The name of an account to get the balance for.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getbalance">getbalance</a>
   */
  public double getBalance(String account) throws GenericRpcException;

  /**
   * The getbalance RPC gets the balance in decimal bitcoins across all accounts or for a particular account.
   *
   * @param account The name of an account to get the balance for.
   * @param minConf The minimum number of confirmations
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getbalance">getbalance</a>
   */
  public double getBalance(String account, int minConf) throws GenericRpcException;

  /**
   * The getinfo RPC prints various information about the node and the network.
   *
   * getinfo was removed in 0.16.0 version of Bitcoin Core.
   *
   * @return Information about this node and the network
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getinfo">getinfo</a>
   */
  public Info getInfo() throws GenericRpcException;

  /**
   * The getmininginfo RPC returns various mining-related information.
   *
   * @return Various mining-related information
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getmininginfo">getmininginfo</a>
   */
  public MiningInfo getMiningInfo() throws GenericRpcException;

  /**
   * The createmultisig RPC creates a P2SH multi-signature address.
   *
   * @param nRequired The minimum (m) number of signatures required to spend this m-of-n multisig script
   * @param keys An array of strings with each string being a public key or address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#createmultisig">createmultisig</a>
   */
  MultiSig createMultiSig(int nRequired, List<String> keys) throws GenericRpcException;

  /**
   * The getnetworkinfo RPC returns information about the node’s connection to the network.
   *
   * @return Information about this node’s connection to the network
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getnetworkinfo">getnetworkinfo</a>
   */
  NetworkInfo getNetworkInfo() throws GenericRpcException;

  static interface Info extends Serializable {

    long version();

    long protocolVersion();

    long walletVersion();

    double balance();

    int blocks();

    int timeOffset();

    int connections();

    String proxy();

    double difficulty();

    boolean testnet();

    long keyPoolOldest();

    long keyPoolSize();

    double payTxFee();

    double relayFee();

    String errors();
  }

  static interface MiningInfo extends Serializable {

    int blocks();

    int currentBlockSize();

    int currentBlockWeight();

    int currentBlockTx();

    double difficulty();

    String errors();

    double networkHashps();

    int pooledTx();

    boolean testNet();

    String chain();
  }

  static interface NetTotals extends Serializable {

    long totalBytesRecv();

    long totalBytesSent();

    long timeMillis();

    interface uploadTarget extends Serializable {

      long timeFrame();

      int target();

      boolean targetReached();

      boolean serveHistoricalBlocks();

      long bytesLeftInCycle();

      long timeLeftInCycle();
    }

    uploadTarget uploadTarget();
  }

  static interface BlockChainInfo extends Serializable {

    String chain();

    int blocks();

    String bestBlockHash();

    double difficulty();

    double verificationProgress();

    String chainWork();
  }

  static interface DecodedScript extends Serializable {

    String asm();

    String hex();

    String type();

    List<String> addresses();

    String p2sh();
  }

  /**
   * The gettxoutsetinfo RPC returns statistics about the confirmed unspent transaction output (UTXO) set.
   * Note that this call may take some time and that it only counts outputs from confirmed transactions—it does not count outputs from the memory pool.
   *
   * @return Information about the UTXO set
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#gettxoutsetinfo">gettxoutsetinfo</a>
   */
  TxOutSetInfo getTxOutSetInfo();

  /**
   * The getwalletinfo RPC provides information about the wallet.
   *
   * @return An object describing the wallet
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getwalletinfo">getwalletinfo</a>
   */
  WalletInfo getWalletInfo();

  static interface WalletInfo extends Serializable {

    long walletVersion();

    BigDecimal balance();

    BigDecimal unconfirmedBalance();

    BigDecimal immatureBalance();

    long txCount();

    long keyPoolOldest();

    long keyPoolSize();

    long unlockedUntil();

    BigDecimal payTxFee();

    String hdMasterKeyId();
  }

  static interface NetworkInfo extends Serializable {

    long version();

    String subversion();

    long protocolVersion();

    String localServices();

    boolean localRelay();

    long timeOffset();

    long connections();

    List<Network> networks();

    BigDecimal relayFee();

    List<String> localAddresses();

    String warnings();
  }

  static interface Network extends Serializable {

    String name();

    boolean limited();

    boolean reachable();

    String proxy();

    boolean proxyRandomizeCredentials();
  }

  static interface MultiSig extends Serializable {

    String address();

    String redeemScript();
  }

  static interface NodeInfo extends Serializable {

    String addedNode();

    boolean connected();

    List<Address> addresses();

  }

  static interface Address extends Serializable {

    String address();

    String connected();
  }

  static interface LockedUnspent extends Serializable {

    String txId();

    int vout();
  }

  static interface TxOut extends Serializable {

    String bestBlock();

    long confirmations();

    BigDecimal value();

    String asm();

    String hex();

    long reqSigs();

    String type();

    List<String> addresses();

    long version();

    boolean coinBase();

  }

  static interface Block extends Serializable {

    String hash();

    int confirmations();

    int size();

    int height();

    int version();

    String merkleRoot();

    List<String> tx();

    List<RawTransaction> rawTx();

    Date time();

    long nonce();

    String bits();

    double difficulty();

    String previousHash();

    String nextHash();

    String chainwork();

    int nTx();

    Block previous() throws GenericRpcException;

    Block next() throws GenericRpcException;
  }

  static interface TxOutSetInfo extends Serializable {

    long height();

    String bestBlock();

    long transactions();

    long txouts();

    long bytesSerialized();

    String hashSerialized();

    BigDecimal totalAmount();
  }

  /**
   * Gets a block at the given height from the local block database.
   *
   * This is a convenience method as a combination of {@link #getBlockHash(int)} and {@link #getBlock(String)}.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblockhash">getblockhash</a>
   * @see <a href="https://bitcoin.org/en/developer-reference#getblock">getblock</a>
   */
  Block getBlock(int height) throws GenericRpcException;

  /**
   * The getblock RPC gets a block with a particular header hash from the local block database either as a JSON object or as a serialized block.
   *
   * @param blockHash The hash of the header of the block to get, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblock">getblock</a>
   */
  Block getBlock(String blockHash) throws GenericRpcException;

  /**
   * The getblock RPC gets a block with a particular header hash from the local block database either as a JSON object or as a serialized block.
   *
   * @param blockHash The hash of the header of the block to get, encoded as hex in RPC byte order
   *
   * @param verbosity If verbosity is 0, returns a string that is serialized, hex-encoded data for block 'hash'.
   * If verbosity is 1, returns an Object with information about block <hash>.
   * If verbosity is 2, returns an Object with information about block <hash> and information about each transaction.
   *
   *  @see <a href="https://bitcoin.org/en/developer-reference#getblock">getblock</a>
   */
  Block getBlock(String blockHash, int verbosity) throws GenericRpcException;

  /**
   * The getblock RPC gets a block with a particular header hash from the local block database as a serialized block.
   *
   * @param blockHash The hash of the header of the block to get, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblock">getblock</a>
   *
   * [TODO] Is this really public API?
   */
  String getRawBlock(String blockHash) throws GenericRpcException;


  /**
   * The getblockheader RPC gets a block header from the local block database either as a JSON object or as a serialized block.
   *
   * @param blockHash The hash of the header of the block to get, encoded as hex in RPC byte order
   *
   * @param verbose If verbose is false, returns a string that is serialized, hex-encoded data for blockheader 'hash'.
   * If verbose is true, returns an Object with information about blockheader 'hash'
   *
   *  @see <a href="https://bitcoin.org/en/developer-reference#getblockheader">getblockheader</a>
   */
  Block getBlockHeader(String blockHash, boolean verbose) throws GenericRpcException;

  /**
   * The getblockhash RPC returns the header hash of a block at the given height in the local best block chain.
   *
   * @param height The height of the block whose header hash should be returned.
   * @return The hash of the block at the requested height, encoded as hex in RPC byte order, or JSON null if an error occurred
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblockhash">getblockhash</a>
   */
  String getBlockHash(int height) throws GenericRpcException;

  /**
   * The getblockchaininfo RPC provides information about the current state of the block chain.
   *
   * @return Information about the current state of the local block chain
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblockchaininfo">getblockchaininfo</a>
   */
  BlockChainInfo getBlockChainInfo() throws GenericRpcException;

  /**
   * The getblockcount RPC returns the number of blocks in the local best block chain.
   *
   * @return The number of blocks in the local best block chain.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getblockcount">getblockcount</a>
   */
  int getBlockCount() throws GenericRpcException;

  /**
   * The getnewaddress RPC returns a new Bitcoin address for receiving payments.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getnewaddress">getnewaddress</a>
   */
  String getNewAddress() throws GenericRpcException;

  /**
   * The getnewaddress RPC returns a new Bitcoin address for receiving payments.
   * If an account is specified, payments received with the address will be credited to that account.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getnewaddress">getnewaddress</a>
   */
  String getNewAddress(String account) throws GenericRpcException;

  /**
   * The getnewaddress RPC returns a new Bitcoin address for receiving payments.
   * If an account is specified, payments received with the address will be credited to that account.
   * The address type to use. Options are "legacy", "p2sh-segwit", and "bech32".
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getnewaddress">getnewaddress</a>
   */
  String getNewAddress(String account, String addressType) throws GenericRpcException;

  /**
   * The getrawmempool RPC returns all transaction identifiers (TXIDs) in the memory pool as a JSON array,
   * or detailed information about each transaction in the memory pool as a JSON object.
   *
   * @return An array of TXIDs belonging to transactions in the memory pool.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getrawmempool">getrawmempool</a>
   */
  List<String> getRawMemPool() throws GenericRpcException;


  /**
   * Returns details on the active state of the TX memory pool.
   *
   * @see <a href="https://bitcoincore.org/en/doc/0.17.0/rpc/blockchain/getmempoolinfo/">getrawmempool</a>
   */
  MemPoolInfo getMemPoolInfo() throws GenericRpcException;

  interface MemPoolInfo extends  Serializable {
    /* Current tx count*/
    int size();

    /* Sum of all virtual transaction sizes as defined in BIP 141. Differs from actual serialized size because witness data is discounted */
    int bytes();

    /* Total memory usage for the mempool */
    long usage();

    /* Maximum memory usage for the mempool */
    long maxmempool();

    /* Minimum fee rate in BTC/kB for tx to be accepted. Is the maximum of minrelaytxfee and minimum mempool fee */
    double mempoolminfee();

    /* Current minimum relay fee for transactions */
    double minrelaytxfee();

  }


  /**
   *  If txid is in the mempool, returns all in-mempool descendants.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getmempooldescendants">getmempooldescendants</a>
   */
  List<String> getMemPoolDescendants(String txId);

  /**
   * The getbestblockhash RPC returns the header hash of the most recent block on the best block chain.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getbestblockhash">getbestblockhash</a>
   */
  String getBestBlockHash() throws GenericRpcException;

  interface RawTransaction extends Serializable {

    String hex();

    String txId();

    int version();

    long lockTime();

    long size();

    long vsize();

    String hash();

    /**
     * get the height of block in which this transaction is</br>
     * <b>PAY ATTENSION</b> this properties only works with bitcoind nodes of which version is under 0.15
     */
    Long height();

    /*
     *
     */
    interface In extends TxInput, Serializable {

      Map<String, Object> scriptSig();

      long sequence();

      RawTransaction getTransaction();

      Out getTransactionOutput();

      /**
       * get the spent address, which is specified in the 'scriptPubKey' in the connected output of this tx </br>
       * <b>PAY ATTENTION</b> This property is only supported by those bitcoind which supports a '-spentindex' option<br/>
       * For more information about 'spentindex', take a look at <a href="https://github.com/satoshilabs/bitcoin">satoshilabs/bitcoin</a>
       * or <a href="https://github.com/bitpay/bitcoin">bitpay/bitcoin</a>
       */
      String address();
    }

    /**
     * This method should be replaced someday
     *
     * @return the list of inputs
     */
    List<In> vIn(); // TODO : Create special interface instead of this

    interface Out extends Serializable {

      double value();

      int n();

      interface ScriptPubKey extends Serializable {

        String asm();

        String hex();

        String type();

        List<String> addresses();
      }

      ScriptPubKey scriptPubKey();

      TxInput toInput();

      RawTransaction transaction();
    }

    /**
     * This method should be replaced someday
     */
    List<Out> vOut(); // TODO : Create special interface instead of this

    String blockHash();

    /**
     * @return null if this tx has not been confirmed yet
     */
    Integer confirmations();

    /**
     *
     * @return null if this tx has not been confirmed yet
     */
    Date time();

    /**
     *
     * @return null if this tx has not been confirmed yet
     */
    Date blocktime();
  }

  /**
   * The getrawtransaction RPC gets a hex-encoded serialized transaction or a JSON object describing the transaction.
   * By default, Bitcoin Core only stores complete transaction data for UTXOs and your own transactions,
   * so the RPC may fail on historic transactions unless you use the non-default txindex=1 in your Bitcoin Core startup settings.
   *
   * @param txId The TXID of the transaction to get, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getrawtransaction">getrawtransaction</a>
   */
  RawTransaction getRawTransaction(String txId) throws GenericRpcException;

  /**
   * The getrawtransaction RPC gets a hex-encoded serialized transaction.
   *
   * @param txId The TXID of the transaction to get, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getrawtransaction">getrawtransaction</a>
   */
  String getRawTransactionHex(String txId) throws GenericRpcException;

  /**
   * The decoderawtransaction RPC gets a hex-encoded serialized transaction or a JSON object describing the transaction.
   * By default, Bitcoin Core only stores complete transaction data for UTXOs and your own transactions,
   * so the RPC may fail on historic transactions unless you use the non-default txindex=1 in your Bitcoin Core startup settings.
   *
   * @param hex The hex body of the transaction to get
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getrawtransaction">getrawtransaction</a>
   */
  RawTransaction decodeRawTransaction(String hex) throws GenericRpcException;

  /**
   * The getreceivedbyaddress RPC returns the total amount received by the specified address in transactions with the specified number of confirmations.
   * It does not count coinbase transactions.
   *
   * @param address The address whose transactions should be tallied
   * @return The number of bitcoins received by the address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getreceivedbyaddress">getreceivedbyaddress</a>
   */
  double getReceivedByAddress(String address) throws GenericRpcException;

  /**
   * The getreceivedbyaddress RPC returns the total amount received by the specified address in transactions with the specified number of confirmations.
   * It does not count coinbase transactions.
   *
   * @param address The address whose transactions should be tallied
   * @param minConf The minimum number of confirmations
   * @return The number of bitcoins received by the address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getreceivedbyaddress">getreceivedbyaddress</a>
   */
  double getReceivedByAddress(String address, int minConf) throws GenericRpcException;

  /**
   * The importprivkey RPC adds a private key to your wallet.
   * The key should be formatted in the wallet import format created by the dumpprivkey RPC.
   *
   * @param bitcoinPrivKey The private key to import into the wallet encoded in base58check using wallet import format (WIF)
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importprivkey">importprivkey</a>
   */
  void importPrivKey(String bitcoinPrivKey) throws GenericRpcException;

  /**
   * The importprivkey RPC adds a private key to your wallet.
   * The key should be formatted in the wallet import format created by the dumpprivkey RPC.
   *
   * @param bitcoinPrivKey The private key to import into the wallet encoded in base58check using wallet import format (WIF)
   * @param account The name of an account to which transactions involving the key should be assigned.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importprivkey">importprivkey</a>
   */
  void importPrivKey(String bitcoinPrivKey, String account) throws GenericRpcException;

  /**
   * The importprivkey RPC adds a private key to your wallet.
   * The key should be formatted in the wallet import format created by the dumpprivkey RPC.
   *
   * @param bitcoinPrivKey The private key to import into the wallet encoded in base58check using wallet import format (WIF)
   * @param account The name of an account to which transactions involving the key should be assigned.
   * @param rescan Set to true (the default) to rescan the entire local block database for transactions affecting any address or pubkey script in the wallet.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importprivkey">importprivkey</a>
   */
  void importPrivKey(String bitcoinPrivKey, String account, boolean rescan) throws GenericRpcException;

  /**
   * The importaddress RPC adds an address or pubkey script to the wallet without the associated private key,
   * allowing you to watch for transactions affecting that address or pubkey script without being able to spend any of its outputs.
   *
   *
   * @param address Either a P2PKH or P2SH address encoded in base58check, or a pubkey script encoded as hex
   * @param account An account name into which the address should be placed.
   * @param rescan Set to true (the default) to rescan the entire local block database
   *
   * @return Null on success.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importaddress">importaddress</a>
   *
   * [TODO] Should this really return Object?
   */
  Object importAddress(String address, String account, boolean rescan) throws GenericRpcException;



  public static class Request implements Serializable {
    ScriptPubKey scriptPubKey;
    Date timestamp;
    boolean watchOnly;
    String label;

    public Request() {}

    public Request(String address, Date timestamp, String label, boolean watchOnly) {
      this.scriptPubKey = new ScriptPubKey();
      this.scriptPubKey.setAddress(address);

      this.label = label;
      this.watchOnly = watchOnly;
      this.timestamp = timestamp;
    }

    public ScriptPubKey getScriptPubKey() {
      return scriptPubKey;
    }

    public void setScriptPubKey(ScriptPubKey scriptPubKey) {
      this.scriptPubKey = scriptPubKey;
    }

    public Date getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(Date timestamp) {
      this.timestamp = timestamp;
    }

    public boolean isWatchOnly() {
      return watchOnly;
    }

    public void setWatchOnly(boolean watchOnly) {
      this.watchOnly = watchOnly;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }

  public static class ScriptPubKey implements Serializable {
    String address;

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
  }

  public static class Options implements Serializable {
    private boolean rescan;

    public Options() {

    }

    public Options(boolean rescan) {
      this.rescan = rescan;
    }

    public boolean isRescan() {
      return rescan;
    }

    public void setRescan(boolean rescan) {
      this.rescan = rescan;
    }
  }

  static interface Error extends Serializable {
    int code();
    String message();
  }

  static interface ImportAddressStatus extends Serializable {
    boolean success();
    List<String> warnings();
    Error error();
  }

  /**
   * Import addresses/scripts (with private or public keys, redeem script (P2SH)), optionally rescanning the blockchain from the earliest creation time of the imported scripts. Requires a new wallet backup.
   * If an address/script is imported without all of the private keys required to spend from that address, it will be watchonly. The 'watchonly' option must be set to true in this case or a warning will be returned.
   * Conversely, if all the private keys are provided and the address/script is spendable, the watchonly option must be set to false, or a warning will be returned.
   *
   * Note: This call can take minutes to complete if rescan is true, during that time, other rpc calls
   * may report that the imported keys, addresses or scripts exists but related transactions are still missin
   *
   * @param requests (array, required) Data to be imported
   * @param options options.
   *
   * @return Response is an array with the same size as the input that has the execution result
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importaddress">importaddress</a>
   *
   */
  List<ImportAddressStatus> importMulti(List<Request> requests, Options options) throws GenericRpcException;


  /**
   * The listaccounts RPC lists accounts and their balances.
   *
   * @return Map that has account names as keys, account balances as values
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listaccounts">listaccounts</a>
   */
  Map<String, Number> listAccounts() throws GenericRpcException;

  /**
   * The listaccounts RPC lists accounts and their balances.
   *
   * @param minConf The minimum number of confirmations an externally-generated transaction must have before it is counted towards the balance.
   * @return Map that has account names as keys, account balances as values
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listaccounts">listaccounts</a>
   */
  Map<String, Number> listAccounts(int minConf) throws GenericRpcException;

  /**
   * The listaccounts RPC lists accounts and their balances.
   *
   * @param minConf The minimum number of confirmations an externally-generated transaction must have before it is counted towards the balance.
   * @param watchonly Include balances in watch-only addresses.
   * @return Map that has account names as keys, account balances as values
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listaccounts">listaccounts</a>
   */
  Map<String, Number> listAccounts(int minConf, boolean watchonly) throws GenericRpcException;

  static interface ReceivedAddress extends Serializable {

    String address();

    String account();

    double amount();

    int confirmations();
  }

  /**
   * The listlockunspent RPC returns a list of temporarily unspendable (locked) outputs.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listlockunspent">listlockunspent</a>
   */
  List<LockedUnspent> listLockUnspent();

  /**
   * The listreceivedbyaddress RPC lists the total number of bitcoins received by each address.
   *
   * @return An array containing objects each describing a particular address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listreceivedbyaddress">listreceivedbyaddress</a>
   */
  List<ReceivedAddress> listReceivedByAddress() throws GenericRpcException;

  /**
   * The listreceivedbyaddress RPC lists the total number of bitcoins received by each address.
   *
   * @param minConf The minimum number of confirmations an externally-generated transaction must have before it is counted towards the balance.
   *
   * @return An array containing objects each describing a particular address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listreceivedbyaddress">listreceivedbyaddress</a>
   */
  List<ReceivedAddress> listReceivedByAddress(int minConf) throws GenericRpcException;

  /**
   * The listreceivedbyaddress RPC lists the total number of bitcoins received by each address.
   *
   * @param minConf The minimum number of confirmations an externally-generated transaction must have before it is counted towards the balance.
   * @param includeEmpty Set to true to display accounts which have never received a payment.
   *
   * @return An array containing objects each describing a particular address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listreceivedbyaddress">listreceivedbyaddress</a>
   */
  List<ReceivedAddress> listReceivedByAddress(int minConf, boolean includeEmpty) throws GenericRpcException;

  static interface Transaction extends Serializable {

    String account();

    String address();

    String category();

    double amount();

    double fee();

    int confirmations();

    String blockHash();

    int blockIndex();

    Date blockTime();

    String txId();

    Date time();

    Date timeReceived();

    String comment();

    String commentTo();

    RawTransaction raw();
  }

  static interface TransactionsSinceBlock extends Serializable {

    List<Transaction> transactions();

    String lastBlock();
  }

  /**
   * The listsinceblock RPC gets all transactions affecting the wallet which have occurred since a particular block, plus the header hash of a block at a particular depth.
   *
   * @return An object containing an array of transactions and the lastblock field
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listsinceblock">listsinceblock</a>
   */
  TransactionsSinceBlock listSinceBlock() throws GenericRpcException;

  /**
   * The listsinceblock RPC gets all transactions affecting the wallet which have occurred since a particular block, plus the header hash of a block at a particular depth.
   *
   * @param blockHash The hash of a block header encoded as hex in RPC byte order.
   * @return An object containing an array of transactions and the lastblock field
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listsinceblock">listsinceblock</a>
   */
  TransactionsSinceBlock listSinceBlock(String blockHash) throws GenericRpcException;

  /**
   * The listsinceblock RPC gets all transactions affecting the wallet which have occurred since a particular block, plus the header hash of a block at a particular depth.
   *
   * @param blockHash The hash of a block header encoded as hex in RPC byte order.
   * @param targetConfirmations Sets the lastblock field of the results to the header hash of a block with this many confirmations.
   * @param includeWatchonly Include transactions to watch-only addresses (see 'importaddress').
   *
   * @return An object containing an array of transactions and the lastblock field
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listsinceblock">listsinceblock</a>
   */
  TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations, boolean includeWatchonly) throws GenericRpcException;

  /**
   * The listtransactions RPC returns the most recent transactions that affect the wallet.
   *
   * @return An array containing objects, with each object describing a payment or internal accounting entry (not a transaction).
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listtransactions">listtransactions</a>
   */
  List<Transaction> listTransactions() throws GenericRpcException;

  /**
   * The listtransactions RPC returns the most recent transactions that affect the wallet.
   *
   * @param account The name of an account to get transactinos from (deprecated).
   *
   * @return An array containing objects, with each object describing a payment or internal accounting entry (not a transaction).
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listtransactions">listtransactions</a>
   */
  List<Transaction> listTransactions(String account) throws GenericRpcException;

  /**
   * The listtransactions RPC returns the most recent transactions that affect the wallet.
   *
   * @param account The name of an account to get transactinos from (deprecated).
   * @param count The number of the most recent transactions to list.
   *
   * @return An array containing objects, with each object describing a payment or internal accounting entry (not a transaction).
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listtransactions">listtransactions</a>
   */
  List<Transaction> listTransactions(String account, int count) throws GenericRpcException;

  /**
   * The listtransactions RPC returns the most recent transactions that affect the wallet.
   *
   * @param account The name of an account to get transactinos from (deprecated).
   * @param count The number of the most recent transactions to list.
   * @param skip The number of the most recent transactions which should not be returned.
   *
   * @return An array containing objects, with each object describing a payment or internal accounting entry (not a transaction).
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listtransactions">listtransactions</a>
   */
  List<Transaction> listTransactions(String account, int count, int skip) throws GenericRpcException;

  interface Unspent extends TxInput, TxOutput, Serializable {

    @Override
    String txid();

    @Override
    int vout();

    @Override
    String address();

    String account();

    String scriptPubKey();

    @Override
    double amount();

    int confirmations();
  }

  /**
   * The listunspent RPC returns an array of unspent transaction outputs belonging to this wallet.
   *
   * @return An array of objects each describing an unspent output.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listunspent">listunspent</a>
   */
  List<Unspent> listUnspent() throws GenericRpcException;

  /**
   * The listunspent RPC returns an array of unspent transaction outputs belonging to this wallet.
   *
   * @param minConf The minimum number of confirmations the transaction containing an output must have in order to be returned.
   *
   * @return An array of objects each describing an unspent output.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listunspent">listunspent</a>
   */
  List<Unspent> listUnspent(int minConf) throws GenericRpcException;

  /**
   * The listunspent RPC returns an array of unspent transaction outputs belonging to this wallet.
   *
   * @param minConf The minimum number of confirmations the transaction containing an output must have in order to be returned.
   * @param maxConf The maximum number of confirmations the transaction containing an output may have in order to be returned.
   *
   * @return An array of objects each describing an unspent output.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listunspent">listunspent</a>
   */
  List<Unspent> listUnspent(int minConf, int maxConf) throws GenericRpcException;

  /**
   * The listunspent RPC returns an array of unspent transaction outputs belonging to this wallet.
   *
   * @param minConf The minimum number of confirmations the transaction containing an output must have in order to be returned.
   * @param maxConf The maximum number of confirmations the transaction containing an output may have in order to be returned.
   * @param addresses Only outputs which pay an address in this array will be returned
   *
   * @return An array of objects each describing an unspent output.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#listunspent">listunspent</a>
   */
  List<Unspent> listUnspent(int minConf, int maxConf, String... addresses) throws GenericRpcException;

  /**
   * The lockunspent RPC temporarily locks or unlocks specified transaction outputs.
   * A locked transaction output will not be chosen by automatic coin selection when spending bitcoins.
   *
   * @param unlock Set to false to lock the outputs specified in the following parameter. Set to true to unlock the outputs specified.
   * @param txid The TXID of the transaction containing the output to lock or unlock, encoded as hex.
   * @param vout The output index number (vout) of the output to lock or unlock.
   *
   * @return true if successful.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#lockunspent">lockunspent</a>
   */
  boolean lockUnspent(boolean unlock, String txid, int vout) throws GenericRpcException;

  /**
   * The move RPC moves a specified amount from one account in your wallet to another using an off-block-chain transaction.
   *
   * @param fromAccount The name of the account to move the funds from
   * @param toAccount The name of the account to move the funds to
   * @param amount The amount of bitcoins to move
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#move">move</a>
   */
  boolean move(String fromAccount, String toAccount, double amount) throws GenericRpcException;

  /**
   * The move RPC moves a specified amount from one account in your wallet to another using an off-block-chain transaction.
   *
   * @param fromAccount The name of the account to move the funds from
   * @param toAccount The name of the account to move the funds to
   * @param amount The amount of bitcoins to move
   * @param comment A comment to assign to this move payment
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#move">move</a>
   */
  boolean move(String fromAccount, String toAccount, double amount, String comment) throws GenericRpcException;

  /**
   * @see <a href="https://bitcoin.org/en/developer-reference#move">move</a>
   * @deprecated
   */
  boolean move(String fromAccount, String toAccount, double amount, int minConf) throws GenericRpcException;

  /**
   * @see <a href="https://bitcoin.org/en/developer-reference#move">move</a>
   * @deprecated
   */
  boolean move(String fromAccount, String toAccount, double amount, int minConf, String comment) throws GenericRpcException;

  /**
   * The sendfrom RPC spends an amount from a local account to a bitcoin address.
   *
   * @param fromAccount The name of the account from which the bitcoins should be spent.
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spend in bitcoins.
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendfrom">sendfrom</a>
   */
  String sendFrom(String fromAccount, String toAddress, double amount) throws GenericRpcException;

  /**
   * The sendfrom RPC spends an amount from a local account to a bitcoin address.
   *
   * @param fromAccount The name of the account from which the bitcoins should be spent.
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spend in bitcoins.
   * @param minConf The minimum number of confirmations an incoming transaction must have for its outputs to be credited to this account’s balance.
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendfrom">sendfrom</a>
   */
  String sendFrom(String fromAccount, String toAddress, double amount, int minConf) throws GenericRpcException;

  /**
   * The sendfrom RPC spends an amount from a local account to a bitcoin address.
   *
   * @param fromAccount The name of the account from which the bitcoins should be spent.
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spend in bitcoins.
   * @param minConf The minimum number of confirmations an incoming transaction must have for its outputs to be credited to this account’s balance.
   * @param comment A locally-stored (not broadcast) comment assigned to this transaction.
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendfrom">sendfrom</a>
   */
  String sendFrom(String fromAccount, String toAddress, double amount, int minConf, String comment) throws GenericRpcException;

  /**
   * The sendfrom RPC spends an amount from a local account to a bitcoin address.
   *
   * @param fromAccount The name of the account from which the bitcoins should be spent.
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spend in bitcoins.
   * @param minConf The minimum number of confirmations an incoming transaction must have for its outputs to be credited to this account’s balance.
   * @param comment A locally-stored (not broadcast) comment assigned to this transaction.
   * @param commentTo A locally-stored (not broadcast) comment assigned to this transaction
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendfrom">sendfrom</a>
   */
  String sendFrom(String fromAccount, String toAddress, double amount, int minConf, String comment, String commentTo) throws GenericRpcException;

  /**
   * The sendrawtransaction RPC validates a transaction and broadcasts it to the peer-to-peer network.
   *
   * @param hex The serialized transaction to broadcast encoded as hex
   *
   * @return If the transaction was accepted by the node for broadcast, this will be the TXID of the transaction encoded as hex in RPC byte order.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendrawtransaction">sendrawtransaction</a>
   */
  String sendRawTransaction(String hex) throws GenericRpcException;

  /**
   * The sendtoaddress RPC spends an amount to a given address.
   *
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spent in bitcoins
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendtoaddress">sendtoaddress</a>
   */
  String sendToAddress(String toAddress, double amount) throws GenericRpcException;

  /**
   * The sendtoaddress RPC spends an amount to a given address.
   *
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spent in bitcoins
   * @param comment A locally-stored (not broadcast) comment assigned to this transaction.
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendtoaddress">sendtoaddress</a>
   */
  String sendToAddress(String toAddress, double amount, String comment) throws GenericRpcException;

  /**
   * The sendtoaddress RPC spends an amount to a given address.
   *
   * @param toAddress A P2PKH or P2SH address to which the bitcoins should be sent
   * @param amount The amount to spent in bitcoins
   * @param comment A locally-stored (not broadcast) comment assigned to this transaction.
   * @param commentTo A locally-stored (not broadcast) comment assigned to this transaction
   *
   * @return The TXID of the sent transaction, encoded as hex in RPC byte order
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#sendtoaddress">sendtoaddress</a>
   */
  String sendToAddress(String toAddress, double amount, String comment, String commentTo) throws GenericRpcException;

  /**
   * The signrawtransaction RPC signs a transaction in the serialized transaction format using private keys stored in the wallet or provided in the call.
   *
   * @param hex The transaction to sign as a serialized transaction
   * @param privateKeys An array holding private keys.
   * @param inputs The previous outputs being spent by this transaction
   *
   * @return The results of the signature
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#signrawtransaction">signrawtransaction</a>
   */
  String signRawTransactionWithKey(String hex, List<String> privateKeys, List<? extends TxInput> inputs) throws GenericRpcException;

  /**
   * The signrawtransaction RPC signs a transaction in the serialized transaction format using private keys stored in the wallet or provided in the call.
   *
   * @param hex The transaction to sign as a serialized transaction
   * @param privateKeys An array holding private keys.
   *
   * @return The results of the signature
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#signrawtransaction">signrawtransaction</a>
   */
  String signRawTransactionWithKey(String hex, List<String> privateKeys) throws GenericRpcException;

  static interface AddressValidationResult extends Serializable {

    boolean isValid();

    String address();

    boolean isMine();

    boolean isScript();

    String pubKey();

    boolean isCompressed();

    String account();
  }

  /**
   * @see <a href="https://bitcoin.org/en/developer-reference#setgenerate">setgenerate</a>
   * @deprecated
   */
  void setGenerate(boolean doGenerate) throws BitcoinRPCException;

  /**
   * The generate RPC nearly instantly generates blocks.
   *
   * @param numBlocks The number of blocks to generate.
   * @return An array containing the block header hashes of the generated blocks
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#generate">generate</a>
   */
  List<String> generate(int numBlocks) throws BitcoinRPCException;

  /**
   * The generate RPC nearly instantly generates blocks.
   *
   * @param numBlocks The number of blocks to generate.
   * @param maxTries The maximum number of iterations that are tried to create the requested number of blocks.
   * @return An array containing the block header hashes of the generated blocks
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#generate">generate</a>
   */
  List<String> generate(int numBlocks, long maxTries) throws BitcoinRPCException;

  /**
   * The generatetoaddress RPC mines blocks immediately to a specified address.
   *
   * @param numBlocks The number of blocks to generate.
   * @param address The address to send the newly generated Bitcoin to
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#generatetoaddress">generatetoaddress</a>
   */
  List<String> generateToAddress(int numBlocks, String address) throws BitcoinRPCException;

  /**
   * The validateaddress RPC returns information about the given Bitcoin address.
   *
   * @param address The P2PKH or P2SH address to validate encoded in base58check format
   * @return Information about the address
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#validateaddress">validateaddress</a>
   */
  AddressValidationResult validateAddress(String address) throws GenericRpcException;

  /**
   * The estimatefee RPC estimates the transaction fee per kilobyte that needs to be paid for a transaction to be included within a certain number of blocks.
   *
   * @param nBlocks The maximum number of blocks a transaction should have to wait before it is predicted to be included in a block.
   * @return The estimated fee the transaction should pay in order to be included within the specified number of blocks.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#estimatefee">estimatefee</a>
   */
  double getEstimateFee() throws GenericRpcException;

  /**
   * The estimatepriority RPC estimates the priority (coin age) that a transaction needs in order to be included within a certain number of blocks as a free high-priority transaction.
   * This should not to be confused with the prioritisetransaction RPC which will remain supported for adding fee deltas to transactions.
   *
   * estimatepriority has been removed and will no longer be available in the next major release (planned for Bitcoin Core 0.15.0).
   *
   * @param nBlocks The maximum number of blocks a transaction should have to wait before it is predicted to be included in a block based purely on its priority
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#estimatepriority">estimatepriority</a>
   */
  double getEstimatePriority(int nBlocks) throws GenericRpcException;

  /**
   * Permanently marks a block as invalid, as if it violated a consensus rule.
   *
   * @param blockHash the hash of the block to mark as invalid
   *
   * [TODO] Add to https://bitcoin.org/en/developer-reference
   */
  void invalidateBlock(String blockHash) throws GenericRpcException;

  /**
   * Removes invalidity status of a block and its descendants, reconsider them for activation.
   * This can be used to undo the effects of invalidateblock.
   *
   * @param blockHash The hash of the block to reconsider
   *
   * [TODO] Add to https://bitcoin.org/en/developer-reference
   */
  void reconsiderBlock(String blockHash) throws GenericRpcException;

  static interface PeerInfoResult extends Serializable {

    long getId();

    String getAddr();

    String getAddrLocal();

    String getServices();

    long getLastSend();

    long getLastRecv();

    long getBytesSent();

    long getBytesRecv();

    long getConnTime();

    int getTimeOffset();

    double getPingTime();

    long getVersion();

    String getSubVer();

    boolean isInbound();

    int getStartingHeight();

    long getBanScore();

    int getSyncedHeaders();

    int getSyncedBlocks();

    boolean isWhiteListed();
  }

  /**
   * The getpeerinfo RPC returns data about each connected network node.
   *
   * @return An array of objects each describing one connected node.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getpeerinfo">getpeerinfo</a>
   */
  List<PeerInfoResult> getPeerInfo();

  /**
   * The stop RPC safely shuts down the Bitcoin Core server.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#stop">stop</a>
   */
  void stop();

  /**
   * The getrawchangeaddress RPC returns a new Bitcoin address for receiving change. This is for use with raw transactions, not normal use.
   *
   * @return A P2PKH address which has not previously been returned by this RPC.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getrawchangeaddress">getrawchangeaddress</a>
   */
  String getRawChangeAddress();

  /**
   * The getconnectioncount RPC returns the number of connections to other nodes.
   *
   * @return The total number of connections to other nodes (both inbound and outbound)
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getconnectioncount">getconnectioncount</a>
   */
  long getConnectionCount();

  /**
   * The getunconfirmedbalance RPC returns the wallet’s total unconfirmed balance.
   *
   * @return The total number of bitcoins paid to this wallet in unconfirmed transactions
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getunconfirmedbalance">getunconfirmedbalance</a>
   */
  double getUnconfirmedBalance();

  /**
   * The getdifficulty RPC
   *
   * @return The difficulty of creating a block with the same target threshold (nBits) as the highest-height block in the local best block chain.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getdifficulty">getdifficulty</a>
   */
  double getDifficulty();

  /**
   * The ping RPC sends a P2P ping message to all connected nodes to measure ping time.
   * Results are provided by the getpeerinfo RPC pingtime and pingwait fields as decimal seconds.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#ping">ping</a>
   */
  void ping();

  /**
   * The decodescript RPC decodes a hex-encoded P2SH redeem script.
   *
   * @param hex The redeem script to decode as a hex-encoded serialized script
   * @return An object describing the decoded script, or JSON null if the script could not be decoded
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#decodescript">decodescript</a>
   */
  DecodedScript decodeScript(String hex);

  /**
   * The getnettotals RPC returns information about network traffic, including bytes in, bytes out, and the current time.
   *
   * @return An object containing information about the node’s network totals
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getnettotals">getnettotals</a>
   */
  NetTotals getNetTotals();

  /**
   * The getgenerate RPC was removed in Bitcoin Core 0.13.0.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getgenerate">getgenerate</a>
   */
  boolean getGenerate();

  /**
   * The getnetworkhashps RPC returns the estimated current or historical network hashes per second based on the last n blocks.
   *
   * @return The estimated number of hashes per second based on the parameters provided.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getgenerate">getgenerate</a>
   *
   * [TODO] Add parameters blocks, height
   */
  double getNetworkHashPs();

  /**
   * The settxfee RPC sets the transaction fee per kilobyte paid by transactions created by this wallet.
   *
   * @param amount The transaction fee to pay, in bitcoins, for each kilobyte of transaction data.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#settxfee">settxfee</a>
   */
  boolean setTxFee(BigDecimal amount);

  /**
   * The addnode RPC attempts to add or remove a node from the addnode list, or to try a connection to a node once.
   *
   * @param node The node to add as a string in the form of <IP address>:<port>.
   * @param command What to do with the IP address above.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#addnode">addnode</a>
   */
  void addNode(String node, String command);

  /**
   * The listwallets RPC returns a list of currently loaded wallets.
   *
   * @return List that has currently loaded wallet names.
   *
   * @see <a href="https://bitcoincore.org/en/doc/22.0.0/rpc/wallet/listwallets/">listwallets</a>
   */
  List<String> listWallets() throws GenericRpcException;

  /**
   * The backupwallet RPC safely copies wallet.dat to the specified file, which can be a directory or a path with filename.
   *
   * @param destination A filename or directory name.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#backupwallet">backupwallet</a>
   */
  void backupWallet(String destination);

  /**
   * The signmessage RPC signs a message with the private key of an address.
   *
   * @param adress A P2PKH address whose private key belongs to this wallet
   * @param message The message to sign
   *
   * @return The signature of the message, encoded in base64.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#signmessage">signmessage</a>
   */
  String signMessage(String adress, String message);

  static interface CreateWalletStatus extends Serializable {
    String name();
    String warning();
  }

  /**
   * Unloads the wallet referenced by the request endpoint otherwise unloads the wallet specified in the argument.
   *
   * @param walletName (string, optional, default=the wallet name from the RPC endpoint) The name of the wallet to unload. Must be provided in the RPC endpoint or this parameter (but not both).
   * @param loadOnStartup (boolean, optional, default=null) Save wallet name to persistent settings and load on startup. True to add wallet to startup list, false to remove, null to leave unchanged.
   *
   * @see <a href="https://bitcoincore.org/en/doc/0.21.0/rpc/wallet/unloadwallet/">unloadwallet</a>
   */
  CreateWalletStatus unloadWallet(String walletName, boolean loadOnStartup);

  /**
   * Creates and loads a new wallet.
   *
   * @param walletName The name for the new wallet.
   * @param disablePrivateKeys (default=false) Disable the possibility of private keys (only watchonlys are possible in this mode).
   *
   * @see <a href="https://bitcoincore.org/en/doc/23.0.0/rpc/wallet/createwallet/">createwallet</a>
   */
  CreateWalletStatus createWallet(String walletName, boolean disablePrivateKeys);

  /**
   * Creates and loads a new wallet.
   *
   * @param walletName The name for the new wallet.
   * @param disablePrivateKeys (default=false) Disable the possibility of private keys (only watchonlys are possible in this mode).
   * @param descriptors (boolean, optional, default=true) Create a native descriptor wallet. The wallet will use descriptors internally to handle address creation.
   *
   * @see <a href="https://bitcoincore.org/en/doc/23.0.0/rpc/wallet/createwallet/">createwallet</a>
   */
  CreateWalletStatus createWallet(String walletName, boolean disablePrivateKeys, boolean descriptors);

  /**
   * The dumpwallet RPC creates or overwrites a file with all wallet keys in a human-readable format.
   *
   * @param filename The file in which the wallet dump will be placed.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#dumpwallet">dumpwallet</a>
   */
  void dumpWallet(String filename);

  /**
   * The importwallet RPC imports private keys from a file in wallet dump file format (see the dumpwallet RPC).
   * These keys will be added to the keys currently in the wallet.
   *
   * @param filename The file to import. The path is relative to Bitcoin Core’s working directory
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#importwallet">importwallet</a>
   */
  void importWallet(String filename);

  /**
   * The keypoolrefill RPC fills the cache of unused pre-generated keys (the keypool).
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#keypoolrefill">keypoolrefill</a>
   */
  void keyPoolRefill();

  /**
   * The getreceivedbyaccount RPC returns the total amount received by addresses in a particular account from transactions with the specified number of confirmations.
   *
   * @param account The name of the account containing the addresses to get.
   * @return The number of bitcoins received by the account.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getreceivedbyaccount">getreceivedbyaccount</a>
   */
  BigDecimal getReceivedByAccount(String account);

  /**
   * The encryptwallet RPC encrypts the wallet with a passphrase.
   * This is only to enable encryption for the first time.
   * After encryption is enabled, you will need to enter the passphrase to use private keys.
   *
   * @param passPhrase The passphrase to use for the encrypted wallet.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#encryptwallet">encryptwallet</a>
   */
  void encryptWallet(String passPhrase);

  /**
   * The walletpassphrase RPC stores the wallet decryption key in memory for the indicated number of seconds.
   * Issuing the walletpassphrase command while the wallet is already unlocked will set a new unlock time that overrides the old one.
   *
   * @param passPhrase The passphrase that unlocks the wallet
   * @param timeOut The number of seconds after which the decryption key will be automatically deleted from memory
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#walletpassphrase">walletpassphrase</a>
   */
  void walletPassPhrase(String passPhrase, long timeOut);

  /**
   * The verifymessage RPC verifies a signed message.
   *
   * @param address The P2PKH address corresponding to the private key which made the signature.
   * @param signature The signature created by the signer encoded as base-64 (the format output by the signmessage RPC)
   * @param message The message exactly as it was signed
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#verifymessage">verifymessage</a>
   */
  boolean verifyMessage(String address, String signature, String message);

  /**
   * The addmultisigaddress RPC adds a P2SH multisig address to the wallet.
   *
   * @param nRequired The minimum (m) number of signatures required to spend this m-of-n multisig script
   * @param keyObject An array of strings with each string being a public key or address
   * @return The P2SH multisig address.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#addmultisigaddress">addmultisigaddress</a>
   */
  String addMultiSigAddress(int nRequired, List<String> keyObject);

  /**
   * The addmultisigaddress RPC adds a P2SH multisig address to the wallet.
   *
   * @param nRequired The minimum (m) number of signatures required to spend this m-of-n multisig script
   * @param keyObject An array of strings with each string being a public key or address
   * @param account The account name in which the address should be stored.
   *
   * @return The P2SH multisig address.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#addmultisigaddress">addmultisigaddress</a>
   */
  String addMultiSigAddress(int nRequired, List<String> keyObject, String account);

  /**
   * The verifychain RPC verifies each entry in the local block chain database.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#verifychain">verifychain</a>
   *
   * [TODO] Add parameters checkLevel, numOfBlocks
   */
  boolean verifyChain();

  /**
   * The getaddednodeinfo RPC returns information about the given added node, or all added nodes (except onetry nodes).
   * Only nodes which have been manually added using the addnode RPC will have their information displayed.
   *
   * @param details Removed in Bitcoin Core 0.14.0
   * @param node The node to get information about in the same <IP address>:<port> format as the addnode RPC.
   * @return An array containing objects describing each added node.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#getaddednodeinfo">getaddednodeinfo</a>
   */
  List<NodeInfo> getAddedNodeInfo(boolean details, String node);

  /**
   * The submitblock RPC accepts a block, verifies it is a valid addition to the block chain, and broadcasts it to the network.
   *
   * @param hexData The full block to submit in serialized block format as hex
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#submitblock">submitblock</a>
   */
  void submitBlock(String hexData);

  /**
   * The gettransaction RPC gets detailed information about an in-wallet transaction.
   *
   * @param txId The TXID of the transaction to get details about.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#gettransaction">gettransaction</a>
   */
  Transaction getTransaction(String txId);

  /**
   * The gettransaction RPC gets detailed information about an in-wallet transaction.
   *
   * @param txId The TXID of the transaction to get details about.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#gettransaction">gettransaction</a>
   */
  Transaction getTransaction(String txId, boolean includeWatchonly);

  /**
   * The gettxout RPC returns details about an unspent transaction output (UTXO).
   *
   * @param txId The TXID of the transaction containing the output to get, encoded as hex in RPC byte order
   * @param vout The output index number (vout) of the output within the transaction
   *
   * @return Information about the output.
   *
   * @see <a href="https://bitcoin.org/en/developer-reference#gettxout">gettxout</a>
   */
  TxOut getTxOut(String txId, long vout);

  static interface SmartFeeResult extends Serializable {

    double feeRate();

    int blocks();

    List<String> errors();
  }

  /**
   * Estimates the approximate fee per kilobyte needed for a transaction to begin confirmation within conf_target blocks if possible
   * and return the number of blocks for which the estimate is valid. Uses virtual transaction size as defined in BIP 141 (witness data is discounted).
   *
   * @param blocks Confirmation target in blocks
   * @return estimate fee rate in BTC/kB
   *
   * [TODO] Add to https://bitcoin.org/en/developer-reference
   */
  SmartFeeResult getEstimateSmartFee(int blocks);

  static enum EstimateMode {
    UNSET,
    ECONOMICAL,
    CONSERVATIVE
  }

  /**
   * Estimates the approximate fee per kilobyte needed for a transaction to begin confirmation within conf_target blocks if possible
   * and return the number of blocks for which the estimate is valid. Uses virtual transaction size as defined in BIP 141 (witness data is discounted).
   *
   * @param blocks Confirmation target in blocks
   * @param estimateMode he fee estimate mode. Whether to return a more conservative estimate which also satisfies a longer history.
   *        A conservative estimate potentially returns a higher feerate and is more likely to be sufficient for the desired
   *        target, but is not as responsive to short term drops in the prevailing fee market.  Must be one of:
   *        "UNSET" (defaults to CONSERVATIVE), "ECONOMICAL", "CONSERVATIVE"
   * @return estimate fee rate in BTC/kB
   *
   * [TODO] Add to https://bitcoin.org/en/developer-reference
   */
  SmartFeeResult getEstimateSmartFee(int blocks, EstimateMode estimateMode);

  /**
   * the result returned by
   * {@link BitcoinJSONRPCClient#getAddressBalance(String)}
   *
   * @author frankchen
   * @create 2018年6月21日 上午10:38:17
   */
  static interface AddressBalance
  {
    long getBalance();
    long getReceived();
  }

  /**
   * the result return by {@link BitcoinJSONRPCClient#getAddressUtxo(String)}
   * @author frankchen
   * @create 2018年6月21日 上午10:38:17
   */
  static interface AddressUtxo
  {
    String getAddress();
    String getTxid();
    int getOutputIndex();
    String getScript();
    long getSatoshis();
    long getHeight();
  }

  /**
   * get the balance of specified address</br>
   * <b>PAY ATTENTION</b>
   * This API only works on some bitcoind nodes which support <i>addressindex</i> option</br>
   * <a href="https://github.com/satoshilabs/bitcoin">satoshilabs/bitcoin</a> is such a kind of these nodes
   */
  AddressBalance getAddressBalance(String address);

  /**
   * get all the utxo list of a specified address
   * <b>PAY ATTENTION</b>
   * This API only works on some bitcoind nodes which support <i>addressindex</i> option</br>
   * <a href="https://github.com/satoshilabs/bitcoin">satoshilabs/bitcoin</a> is such a kind of these nodes
   */
  List<AddressUtxo> getAddressUtxo(String address);

  /**
   * Converts a network serialized transaction to a PSBT
   * (<a href="https://bitcoincore.org/en/doc/25.0.0/rpc/rawtransactions/converttopsbt/">converttopsbt</a>).
   * This should be used only with createrawtransaction and fundrawtransaction
   * createpsbt and walletcreatefundedpsbt should be used for new applications.
   *
   * @param rawTransaction  The hex string of a raw transaction
   *
   *
   */
  String convertToPSBT(String rawTransaction);


  /**
   * Creates a transaction in the Partially Signed Transaction format.
   * Implements the Creator role.
   *
   * @param inputs An array of objects, each one to be used as an input to the transaction
   * @param outputs The addresses and amounts to pay
   * @param locktime (numeric, optional, default=0) Raw locktime. Non-0 value also locktime-activates inputs
   * @param replaceable (boolean, optional, default=false) Marks this transaction as BIP125-replaceable.
   * @return The resulting raw transaction (base64-encoded string).
   *
   * @see <a href="https://bitcoincore.org/en/doc/23.0.0/rpc/rawtransactions/createpsbt/">createpsbt</a>
   */
  String createPSBT(List<TxInput> inputs, List<TxOutput> outputs, int locktime, boolean replaceable) throws GenericRpcException;


  /**
   * Return a JSON object representing the serialized, base64-encoded partially signed Bitcoin transaction.
   *
   * @param psbt The PSBT base64 string
   *
   * @see <a href="https://bitcoincore.org/en/doc/23.0.0/rpc/rawtransactions/decodepsbt/">decodepsbt</a>
   */
  RawTransaction decodePSBT(String psbt) throws GenericRpcException;

  /**
   * Analyzes and provides information about the current status of a PSBT and its inputs
   *
   * @param psbt The PSBT base64 string
   *
   * @see <a href="https://bitcoincore.org/en/doc/23.0.0/rpc/rawtransactions/analyzepsbt/">analyzepsbt</a>
   */
  Map analyzePSBT(String psbt) throws GenericRpcException;
}
