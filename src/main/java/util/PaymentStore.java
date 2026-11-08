package util;

import model.entity.DonHang;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - THREAD-SAFE PAYMENT STORE (IN-MEMORY CACHE)
 * Speeds up VietQR and Webhook payment confirmations without database thrashing.
 * =========================================================================
 */
public class PaymentStore {
    private static final Logger LOGGER = Logger.getLogger(PaymentStore.class.getName());

    // Bộ nhớ đệm cục bộ dạng Thread-safe ConcurrentHashMap
    private static final ConcurrentHashMap<String, Boolean> localTransactions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, DonHang> localPendingOrders = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, Boolean> transactions = localTransactions;
    public static final ConcurrentHashMap<String, DonHang> pendingOrders = localPendingOrders;

    private PaymentStore() {}

    public static void putTransaction(String key, boolean value) {
        if (key != null) {
            localTransactions.put(key.toUpperCase().trim(), value);
        }
    }

    public static boolean containsTransaction(String key) {
        if (key == null) return false;
        return localTransactions.containsKey(key.trim().toUpperCase());
    }

    public static void removeTransaction(String key) {
        if (key != null) {
            localTransactions.remove(key.trim().toUpperCase());
        }
    }

    public static void putPendingOrder(String orderId, DonHang order) {
        if (orderId != null && order != null) {
            localPendingOrders.put(orderId.toUpperCase().trim(), order);
        }
    }

    public static DonHang getPendingOrder(String orderId) {
        if (orderId == null) return null;
        return localPendingOrders.get(orderId.trim().toUpperCase());
    }

    public static boolean containsPendingOrder(String orderId) {
        if (orderId == null) return false;
        return localPendingOrders.containsKey(orderId.trim().toUpperCase());
    }

    public static DonHang removePendingOrder(String orderId) {
        if (orderId == null) return null;
        return localPendingOrders.remove(orderId.trim().toUpperCase());
    }
}
