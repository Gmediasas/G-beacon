package com.estimote.proximity.estimote;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.estimote.proximity.R;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentManager {

    private Context context;
    private ProximityContentAdapter proximityContentAdapter;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;
    private boolean firstInteraction = false;
    private boolean secondInteraction = false;

    public ProximityContentManager(Context context, ProximityContentAdapter proximityContentAdapter, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.proximityContentAdapter = proximityContentAdapter;
        this.cloudCredentials = cloudCredentials;
    }

    public void start() {



        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("porkcolombia-gvu")
                .inCustomRange(1.0)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                        List<ProximityContent> nearbyContent = new ArrayList<>(contexts.size());

                        for (ProximityZoneContext proximityContext : contexts) {
                            String title = proximityContext.getAttachments().get("porkcolombia-gvu/title");
                            String identificador = proximityContext.getDeviceId();

                            if (title == null) {
                                title = "unknown";
                            }

                            String subtitle = Utils.getShortIdentifier(proximityContext.getDeviceId());

                            //nearbyContent.add(new ProximityContent(title, subtitle));

                            String nombreDispositivo = "";

                            if(firstInteraction == false && identificador.equals("09e2c798ae8c7b7d6e0b6be131d97b30")) {

                                MediaPlayer mp = MediaPlayer.create(context, R.raw.siren);
                                mp.start();

                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gfashion.gmediacompany.com/contact//"));
                                context.startActivity(myIntent);

                                nombreDispositivo = "Link Evento";
                                firstInteraction = true;
                            }
                            else if(secondInteraction == false  && identificador.equals("9856079b86316bd53fd53ec7924ed814")) {

                                MediaPlayer mp = MediaPlayer.create(context, R.raw.alert);
                                mp.start();

                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://testing.gevents.co/wordpress/producto/cuerpo-banda-lateral/"));
                                context.startActivity(myIntent);

                                nombreDispositivo = "Video Youtube";
                                secondInteraction = true;
                            }
                            else if(identificador.equals("f7be2c2223e89471378ebb8a4f953f27")) {

                                MediaPlayer mp = MediaPlayer.create(context, R.raw.alien);
                                mp.start();

                                //Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://porkcolombia.co/"));
                                //context.startActivity(myIntent);

                                nombreDispositivo = "Link PorkColombia";
                            }

                            nearbyContent.add(new ProximityContent(title, nombreDispositivo));
                        }

                        proximityContentAdapter.setNearbyContent(nearbyContent);
                        proximityContentAdapter.notifyDataSetChanged();

                        return null;
                    }
                })
                .build();

        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }
}
