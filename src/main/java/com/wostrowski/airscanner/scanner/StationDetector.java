package com.wostrowski.airscanner.scanner;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Longs;
import com.wostrowski.airscanner.Log;
import org.codehaus.preon.DecodingException;
import org.tudelft.aircrack.Interface;
import org.tudelft.aircrack.JniInterface;
import org.tudelft.aircrack.TransmitInfo;
import org.tudelft.aircrack.frame.Address;
import org.tudelft.aircrack.frame.Frame;
import org.tudelft.aircrack.frame.control.AckFrame;
import org.tudelft.aircrack.frame.control.BlockAckFrame;
import org.tudelft.aircrack.frame.control.CtsFrame;
import org.tudelft.aircrack.frame.control.RtsFrame;
import org.tudelft.aircrack.frame.data.DataFrame;
import org.tudelft.aircrack.frame.management.ProbeRequest;
import org.tudelft.aircrack.frame.management.ProbeResponse;
import org.tudelft.aircrack.frame.visitor.AbstractFrameVisitor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Detects all devices that are in range of the WIFI adapter signal
 */
public class StationDetector {
    private final StationDetectorListener listener;
    private Thread scannerThread;
    private Scanner scanner;

    /**
     * @param listener listener for stations detection events
     */
    public StationDetector(StationDetectorListener listener) {
        Preconditions.checkNotNull(listener, "listener must not be null");
        this.listener = listener;
    }

    private static class DataVisitor extends AbstractFrameVisitor {
        private final Map<String, Station> stations;

        public DataVisitor(Map<String, Station> stations) {
            this.stations = stations;
        }

        @Override
        public void visit(DataFrame frame) {
            final String add1 = frame.getAddress1() != null ? frame.getAddress1().toString().toLowerCase() : null;
            final String add2 = frame.getAddress2() != null ? frame.getAddress2().toString().toLowerCase() : null;
            final String add3 = frame.getAddress3() != null ? frame.getAddress3().toString().toLowerCase() : null;
            final String add4 = frame.getAddress4() != null ? frame.getAddress4().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.DF_add1 = System.currentTimeMillis();
                station.address = frame.getAddress1();
            }

            if (!Strings.isNullOrEmpty(add2)) {
                Station station = stations.get(add2);
                if (station == null) {
                    station = new Station();
                    stations.put(add2, station);
                }
                station.DF_add2 = System.currentTimeMillis();
                station.address = frame.getAddress2();
            }

            if (!Strings.isNullOrEmpty(add3)) {
                Station station = stations.get(add3);
                if (station == null) {
                    station = new Station();
                    stations.put(add3, station);
                }
                station.DF_add3 = System.currentTimeMillis();
                station.address = frame.getAddress3();
            }

            if (!Strings.isNullOrEmpty(add4)) {
                Station station = stations.get(add4);
                if (station == null) {
                    station = new Station();
                    stations.put(add4, station);
                }
                station.DF_add4 = System.currentTimeMillis();
                station.address = frame.getAddress3();
            }
        }

        @Override
        public void visit(ProbeRequest frame) {
            final String add1 = frame.getSA() != null ? frame.getSA().toString().toLowerCase() : null;
            if (add1 != null) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }

                station.ProbeReq_sa = System.currentTimeMillis();
                station.address = frame.getSA();
            }
        }


        @Override
        public void visit(ProbeResponse frame) {
            final String add1 = frame.getAddress1() != null ? frame.getAddress1().toString().toLowerCase() : null;
            final String add2 = frame.getSA() != null ? frame.getSA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.ProbeResp_addr = System.currentTimeMillis();
                station.address = frame.getAddress1();
            }

            if (!Strings.isNullOrEmpty(add2)) {
                Station station = stations.get(add2);
                if (station == null) {
                    station = new Station();
                    stations.put(add2, station);
                }
                station.ProbeResp_sa = System.currentTimeMillis();
                station.address = frame.getSA();
            }
        }

        @Override
        public void visit(AckFrame frame) {
            final String add1 = frame.getRA() != null ? frame.getRA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getRA();
            }
        }

        @Override
        public void visit(BlockAckFrame frame) {
            final String add1 = frame.getRA() != null ? frame.getRA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getRA();
            }

            final String add2 = frame.getTA() != null ? frame.getTA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add2)) {
                Station station = stations.get(add2);
                if (station == null) {
                    station = new Station();
                    stations.put(add2, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getTA();
            }
        }

        @Override
        public void visit(RtsFrame frame) {
            final String add1 = frame.getRA() != null ? frame.getRA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getRA();
            }

            final String add2 = frame.getTA() != null ? frame.getTA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add2)) {
                Station station = stations.get(add2);
                if (station == null) {
                    station = new Station();
                    stations.put(add2, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getTA();
            }
        }

        @Override
        public void visit(CtsFrame frame) {
            final String add1 = frame.getRA() != null ? frame.getRA().toString().toLowerCase() : null;

            if (!Strings.isNullOrEmpty(add1)) {
                Station station = stations.get(add1);
                if (station == null) {
                    station = new Station();
                    stations.put(add1, station);
                }
                station.ack = System.currentTimeMillis();
                station.address = frame.getRA();
            }
        }
    }

    private static class Station {
        Address address;
        long DF_add1;
        long DF_add2;
        long DF_add3;
        long DF_add4;
        long ProbeReq_sa;
        long ProbeResp_addr;
        long ProbeResp_sa;
        long ack;

        long min() {
            return Longs.min(DF_add1, DF_add2, DF_add3, DF_add4, ProbeReq_sa, ProbeResp_addr, ProbeResp_sa, ack);
        }
    }

    /**
     * Listener for station detection events
     */
    public interface StationDetectorListener {
        /**
         * Called when new device is detected
         *
         * @param stations list of MAC addresses of detected stations
         */
        void onDetect(final String[] stations);
    }


    private class Scanner implements Runnable {
        // Wait time in milliseconds
        private static final long waitTime = 1200;
        private final Map<String, Station> stations = new HashMap<>();
        private final DataVisitor visitor = new DataVisitor(stations);

        @Override
        public void run() {
            Log.d("WIFI scanner started");

            JniInterface iface = null;

            try {
                iface = new JniInterface("mon0");
                iface.open();

                while (true) {
                    try {
                        for (int i = 1; i <= 11; i++) {
                            stations.clear();
                            probeChannel(iface, i);

                            if (!stations.isEmpty()) {
                                final String[] addresses = Iterables.toArray(Iterables.transform(stations.keySet(),
                                        new Function<String, String>() {
                                            @Override
                                            public String apply(String key) {
                                                return stations.get(key).address.toString();
                                            }
                                        }), String.class);

                                listener.onDetect(addresses);
                            }
                        }
                    } catch (InterruptedException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        Log.e("Channels monitoring error, " + ex.getMessage());
                    }
                }
            } catch (InterruptedException ignore) {
            } catch (Exception ex) {
                Log.e("Scanner thread finishing on error", ex);
            } finally {
                if (iface != null) {
                    iface.close();
                }
            }

            Log.d("WIFI scanner stopped");
        }

        private void probeChannel(Interface iface, int channel)
                throws IOException, DecodingException, InterruptedException {
            iface.setChannel(channel);

            // construct probe request
            ProbeRequest probeRequest = new ProbeRequest();
            probeRequest.setAddress1(Address.Broadcast);
            probeRequest.setSA(iface.getMac());
            probeRequest.setBSSID(Address.Broadcast);
            probeRequest.setDuration((int) (waitTime) * 10);

            // Send probe request
            TransmitInfo transmitInfo = new TransmitInfo();
            iface.write(Frame.encode(probeRequest), transmitInfo);
            Thread.yield();

            // Collect probe responses
            final long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < waitTime) {
                Frame frame = iface.receive();

                if (frame != null) {
                    frame.accept(visitor);
                } else {
                    Thread.sleep(100);
                }
            }
        }
    }

    public synchronized void start() {
        if (scannerThread != null) {
            throw new IllegalStateException("Scanner is already running");
        }

        scanner = new Scanner();
        scannerThread = new Thread(scanner);
        scannerThread.start();
    }

    public synchronized void stop() {
        if (scannerThread != null) {
            scannerThread.interrupt();

            try {
                scannerThread.join(3000);
            } catch (InterruptedException e) {
            } finally {
                scannerThread = null;
            }
        }
    }

    public synchronized boolean isRunning() {
        return scannerThread != null;
    }
}
