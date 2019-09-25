import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TradesWindowCalc {

    private final static Map<String, Integer> MAX_AMOUNT       = new HashMap<>();
    private final static Deque<Trade>         TIME_STAMP_QUEUE = new ArrayDeque<>();
    private final static Map<String, Integer> CURRENT_AMOUNT   = new HashMap<>();
    private final static DateFormat           FORMAT           = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
    private final static File                 FILE             = new File("trades.csv");

    public static void main(String[] args) {
        final Scanner scanner;
        try {
            scanner = new Scanner(FILE);
            TradesWindowCalc t = new TradesWindowCalc();
            t.findMaxTrades(scanner);
            scanner.close();
            MAX_AMOUNT.forEach((key, value) -> System.out.println(value));
        } catch (FileNotFoundException | ParseException e) {
            System.err.println(e.getMessage());
        }

    }

    private void findMaxTrades(Scanner scanner) throws ParseException {
        if (scanner.hasNext()) {

            Trade head = new Trade(scanner.next());

            MAX_AMOUNT.put(head.exchange, 1);
            CURRENT_AMOUNT.put(head.exchange, 1);
            TIME_STAMP_QUEUE.add(head);

            while (scanner.hasNext()) {
                Trade tail = new Trade(scanner.next());

                if (isOneMinuteNotExpired(head.date, tail.date)) {
                    if (!TIME_STAMP_QUEUE.contains(tail))
                        calculateMaxAmount(tail);
                } else {
                    CURRENT_AMOUNT.put(head.exchange, CURRENT_AMOUNT.get(head.exchange) - 1);
                    head = tail;
                }

                if (!TIME_STAMP_QUEUE.getLast().date.equals(tail.date))
                    TIME_STAMP_QUEUE.clear();
                TIME_STAMP_QUEUE.add(tail);
            }
        }
    }

    private void calculateMaxAmount(Trade trade) {
        String exchange = trade.exchange;
        if (CURRENT_AMOUNT.containsKey(exchange))
            CURRENT_AMOUNT.put(exchange, CURRENT_AMOUNT.get(exchange) + 1);
        else
            CURRENT_AMOUNT.put(exchange, 1);
        if (MAX_AMOUNT.containsKey(exchange)) {
            if (MAX_AMOUNT.get(exchange) < CURRENT_AMOUNT.get(exchange)) {
                MAX_AMOUNT.put(exchange, CURRENT_AMOUNT.get(exchange));
            }
        } else {
            MAX_AMOUNT.put(exchange, 1);
        }
    }

    static class Trade {
        Calendar date;
        String   exchange;

        Trade(String string) throws ParseException {
            String[] array = string.replaceAll(" ", "").split(",");
            Calendar date1 = Calendar.getInstance();
            date1.setTime(FORMAT.parse(array[0]));
            this.exchange = array[3];
            this.date = date1;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Trade myMap = (Trade) o;
            return date.equals(myMap.date) && exchange.equals(myMap.exchange);

        }
    }

    private static boolean isOneMinuteNotExpired(Calendar d1, Calendar d2) {
        long k = TimeUnit.MILLISECONDS.toMillis(d2.getTime().getTime() - d1.getTime().getTime());
        return k < 60000;
    }


}
