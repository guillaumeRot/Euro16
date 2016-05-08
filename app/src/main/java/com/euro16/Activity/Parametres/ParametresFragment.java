package com.euro16.Activity.Parametres;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.facebook.FacebookActivity;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ParametresFragment extends Fragment {

    private FrameLayout layout;

    public static ParametresFragment newInstance() {
        ParametresFragment fragment = new ParametresFragment();
        return fragment;
    }

    public ParametresFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_parametres, container, false);

        TextView mentionsLegales = (TextView) layout.findViewById(R.id.mentionsLegales);

        mentionsLegales.setText("Novitates autem si spem adferunt, ut tamquam in herbis non fallacibus fructus appareat, non sunt illae quidem repudiandae, vetustas tamen suo loco conservanda; maxima est enim vis vetustatis et consuetudinis. Quin in ipso equo, cuius modo feci mentionem, si nulla res impediat, nemo est, quin eo, quo consuevit, libentius utatur quam intractato et novo. Nec vero in hoc quod est animal, sed in iis etiam quae sunt inanima, consuetudo valet, cum locis ipsis delectemur, montuosis etiam et silvestribus, in quibus diutius commorati sumus.\n" +
                "\n" +
                "Pandente itaque viam fatorum sorte tristissima, qua praestitutum erat eum vita et imperio spoliari, itineribus interiectis permutatione iumentorum emensis venit Petobionem oppidum Noricorum, ubi reseratae sunt insidiarum latebrae omnes, et Barbatio repente apparuit comes, qui sub eo domesticis praefuit, cum Apodemio agente in rebus milites ducens, quos beneficiis suis oppigneratos elegerat imperator certus nec praemiis nec miseratione ulla posse deflecti.\n" +
                "\n" +
                "Cumque pertinacius ut legum gnarus accusatorem flagitaret atque sollemnia, doctus id Caesar libertatemque superbiam ratus tamquam obtrectatorem audacem excarnificari praecepit, qui ita evisceratus ut cruciatibus membra deessent, inplorans caelo iustitiam, torvum renidens fundato pectore mansit inmobilis nec se incusare nec quemquam alium passus et tandem nec confessus nec confutatus cum abiecto consorte poenali est morte multatus. et ducebatur intrepidus temporum iniquitati insultans, imitatus Zenonem illum veterem Stoicum qui ut mentiretur quaedam laceratus diutius, avulsam sedibus linguam suam cum cruento sputamine in oculos interrogantis Cyprii regis inpegit.\n" +
                "\n" +
                "Emensis itaque difficultatibus multis et nive obrutis callibus plurimis ubi prope Rauracum ventum est ad supercilia fluminis Rheni, resistente multitudine Alamanna pontem suspendere navium conpage Romani vi nimia vetabantur ritu grandinis undique convolantibus telis, et cum id inpossibile videretur, imperator cogitationibus magnis attonitus, quid capesseret ambigebat.\n" +
                "\n" +
                "Exsistit autem hoc loco quaedam quaestio subdifficilis, num quando amici novi, digni amicitia, veteribus sint anteponendi, ut equis vetulis teneros anteponere solemus. Indigna homine dubitatio! Non enim debent esse amicitiarum sicut aliarum rerum satietates; veterrima quaeque, ut ea vina, quae vetustatem ferunt, esse debet suavissima; verumque illud est, quod dicitur, multos modios salis simul edendos esse, ut amicitiae munus expletum sit.\n" +
                "\n" +
                "Nihil est enim virtute amabilius, nihil quod magis adliciat ad diligendum, quippe cum propter virtutem et probitatem etiam eos, quos numquam vidimus, quodam modo diligamus. Quis est qui C. Fabrici, M'. Curi non cum caritate aliqua benevola memoriam usurpet, quos numquam viderit? quis autem est, qui Tarquinium Superbum, qui Sp. Cassium, Sp. Maelium non oderit? Cum duobus ducibus de imperio in Italia est decertatum, Pyrrho et Hannibale; ab altero propter probitatem eius non nimis alienos animos habemus, alterum propter crudelitatem semper haec civitas oderit.\n" +
                "\n" +
                "Nec vox accusatoris ulla licet subditicii in his malorum quaerebatur acervis ut saltem specie tenus crimina praescriptis legum committerentur, quod aliquotiens fecere principes saevi: sed quicquid Caesaris implacabilitati sedisset, id velut fas iusque perpensum confestim urgebatur impleri.\n" +
                "\n" +
                "Eius populus ab incunabulis primis ad usque pueritiae tempus extremum, quod annis circumcluditur fere trecentis, circummurana pertulit bella, deinde aetatem ingressus adultam post multiplices bellorum aerumnas Alpes transcendit et fretum, in iuvenem erectus et virum ex omni plaga quam orbis ambit inmensus, reportavit laureas et triumphos, iamque vergens in senium et nomine solo aliquotiens vincens ad tranquilliora vitae discessit.\n" +
                "\n" +
                "Hacque adfabilitate confisus cum eadem postridie feceris, ut incognitus haerebis et repentinus, hortatore illo hesterno clientes numerando, qui sis vel unde venias diutius ambigente agnitus vero tandem et adscitus in amicitiam si te salutandi adsiduitati dederis triennio indiscretus et per tot dierum defueris tempus, reverteris ad paria perferenda, nec ubi esses interrogatus et quo tandem miser discesseris, aetatem omnem frustra in stipite conteres summittendo.\n" +
                "\n" +
                "Hacque adfabilitate confisus cum eadem postridie feceris, ut incognitus haerebis et repentinus, hortatore illo hesterno clientes numerando, qui sis vel unde venias diutius ambigente agnitus vero tandem et adscitus in amicitiam si te salutandi adsiduitati dederis triennio indiscretus et per tot dierum defueris tempus, reverteris ad paria perferenda, nec ubi esses interrogatus et quo tandem miser discesseris, aetatem omnem frustra in stipite conteres summittendo.");

        Button btnDesinscrire = (Button) layout.findViewById(R.id.btnDesinscrire);

        btnDesinscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CurrentSession.communaute != null) {
                    RestClient.deleteUtilisateurCommunaute(CurrentSession.communaute.getNom(), CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // L'utilisateur a bien été supprimé de la communauté
                            Toast.makeText(getActivity().getApplicationContext(), "Vous êtes désinscrit", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            CurrentSession.utilisateur = null;
                            startActivity(new Intent(getActivity(), FacebookConnexion.class));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("Euro 16", "désinscrire : onFailure : " + statusCode);
                        }
                    });
                } else if(CurrentSession.groupe != null) {
                    RestClient.deleteUtilisateurGroupe(CurrentSession.groupe.getNom(), CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // L'utilisateur a bien été supprimé du groupe
                            Toast.makeText(getActivity().getApplicationContext(), "Vous êtes désinscrit", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            CurrentSession.utilisateur = null;
                            startActivity(new Intent(getActivity(), FacebookConnexion.class));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("Euro 16", "désinscrire : onFailure : " + statusCode);
                        }
                    });
                }

            }
        });

        return layout;
    }

}
