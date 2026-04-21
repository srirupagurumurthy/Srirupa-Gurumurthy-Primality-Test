import java.util.*;
import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class PrimeLab {

    // 1. Trial Division O(sqrt n)
    public static boolean trialDivision(long n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        long limit = (long) Math.sqrt(n);
        for (long i = 3; i <= limit; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // 2. Sieve of Eratosthenes up to limit
    public static boolean[] sieveOfEratosthenes(int limit) {
        boolean[] isPrime = new boolean[limit + 1];
        Arrays.fill(isPrime, true);
        if (limit >= 0) isPrime[0] = false;
        if (limit >= 1) isPrime[1] = false;

        for (int i = 2; i * i <= limit; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        return isPrime;
    }

    // 3. Miller-Rabin probabilistic test O(k log^3 n)
    public static boolean millerRabin(long n, int k) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;

        BigInteger bn = BigInteger.valueOf(n);
        BigInteger nMinus1 = bn.subtract(BigInteger.ONE);

        // write n-1 = d * 2^r
        int r = 0;
        BigInteger d = nMinus1;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            r++;
        }

        for (int i = 0; i < k; i++) {
            long aLong = ThreadLocalRandom.current().nextLong(2, n - 1);
            BigInteger a = BigInteger.valueOf(aLong);
            BigInteger x = a.modPow(d, bn);
            if (x.equals(BigInteger.ONE) || x.equals(nMinus1)) continue;

            boolean continueOuter = false;
            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, bn);
                if (x.equals(nMinus1)) {
                    continueOuter = true;
                    break;
                }
            }
            if (!continueOuter) return false;
        }
        return true;
    }

    // 4. Fermat test O(k log n)
    public static boolean fermatTest(long n, int k) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;

        BigInteger bn = BigInteger.valueOf(n);
        BigInteger nMinus1 = bn.subtract(BigInteger.ONE);

        for (int i = 0; i < k; i++) {
            long aLong = ThreadLocalRandom.current().nextLong(2, n - 1);
            BigInteger a = BigInteger.valueOf(aLong);
            if (!a.modPow(nMinus1, bn).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    // 5. Simple benchmark
    public static void benchmark(int maxN) {
        System.out.println("\nBenchmarking up to " + maxN);
        System.out.printf("%-10s %-20s %-20s%n", "Number", "TrialDivision(ms)", "MillerRabin(ms)");
        System.out.println("------------------------------------------------------------");

        for (int size = 1000; size <= maxN; size += 2000) {
            long testNum = size - 1; // test odd numbers, harder case

            long t0 = System.nanoTime();
            boolean td = trialDivision(testNum);
            long t1 = System.nanoTime();

            long t2 = System.nanoTime();
            boolean mr = millerRabin(testNum, 5);
            long t3 = System.nanoTime();

            double tdMs = (t1 - t0) / 1_000_000.0;
            double mrMs = (t3 - t2) / 1_000_000.0;

            System.out.printf("%-10d %-20.4f %-20.4f %s%n",
                    testNum, tdMs, mrMs, td == mr? "" : "<- mismatch");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number to test: ");
        long n = sc.nextLong();

        long t0 = System.nanoTime();
        boolean td = trialDivision(n);
        long t1 = System.nanoTime();

        long t2 = System.nanoTime();
        boolean mr = millerRabin(n, 5);
        long t3 = System.nanoTime();

        long t4 = System.nanoTime();
        boolean ft = fermatTest(n, 5);
        long t5 = System.nanoTime();

        System.out.println("\nResults for " + n + ":");
        System.out.println("------------------------------------------------------------");
        System.out.printf("Trial Division: %b | Time: %.4f ms%n", td, (t1 - t0) / 1_000_000.0);
        System.out.printf("Miller-Rabin: %b | Time: %.4f ms%n", mr, (t3 - t2) / 1_000_000.0);
        System.out.printf("Fermat Test: %b | Time: %.4f ms%n", ft, (t5 - t4) / 1_000_000.0);

        if (td!= mr) {
            System.out.println("\nNote: Probabilistic test disagrees. May be a pseudoprime.");
        }

        System.out.print("\nRun benchmark? y/n: ");
        String choice = sc.next();
        if (choice.equalsIgnoreCase("y")) {
            benchmark(20001);
        }
        sc.close();
    }
}
