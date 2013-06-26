//importaciones para la creacion del activity
import android.os.Bundle;
import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
/*
 para realizar los intents 
(peticiones de servicio a otros activitys de aplicaciones 
instaladas en el SO)
*/
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

public class Camera extends Activity {
	
//	 Constantes para identificar la accion realizada 	 
	private static int TAKE_PICTURE = 1;
	private static int SELECT_PICTURE = 2;
	
		private String name = ""; // Nombre del fichero que contiene la foto 
	
    //metodo de creacion del activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // colocamos nuestro layout 
        
        name = Environment.getExternalStorageDirectory() + "/test.jpg";  // colocamos en la variable name un path absoluto 

        Button btnAction = (Button)findViewById(R.id.btnPic);
        btnAction.setOnClickListener(new OnClickListener() {       		
       		@Override
       		public void onClick(View v) {
       			//Obtenemos los RButtons de galeria y imagen completa   			 
       			RadioButton rbtnFull = (RadioButton)findViewById(R.id.radbtnFull);
       			RadioButton rbtnGallery = (RadioButton)findViewById(R.id.radbtnGall);
       			
       			/**
       			  para todos los casos es necesario un intent, si accesamos la camara con la accion
       			  ACTION_IMAGE_CAPTURE, si accesamos la galeria con la accion ACTION_PICK. 
       			  En el caso de la vista previa no se necesita mas que el intent, 
       			  el codigo e iniciar la actividad. Por eso inicializamos las variables intent y
       			  code con los valores necesarios para el caso de la previsualización y asi lo tenemos listo por si es la opción
			  seleccionada       			 */
       			Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
       			int code = TAKE_PICTURE;
       			
       			/**
       			  si la opcion seleccionada es fotografia completa, necesitamos un archivo donde
       			  guardarla
       			 */
       			if (rbtnFull.isChecked()) {					
       				Uri output = Uri.fromFile(new File(name));
       		    	intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
       			/*
       			  Si la opcion seleccionada es ir a la galeria, el intent es diferente y el codigo
       			  tambien       			 */       		    	
       			} else if (rbtnGallery.isChecked()){       				
       				intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
       				code = SELECT_PICTURE;
       			}
       			
       			/*
       			 * Iniciamos el activity -for result- puesto que esperamos nos devuelva lo que solicitamos!!
       			 */
       			startActivityForResult(intent, code);				
       		}
       	});        
    }
    
    /*
      Funcion que se ejecuta cuando concluye el intent en el que se solicita una imagen
      ya sea de la camara o de la galeria
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	//  Se revisa si la imagen viene de la camara (TAKE_PICTURE) o de la galeria (SELECT_PICTURE)
    	 
    	if (requestCode == TAKE_PICTURE) {
    		
    		 // Si se reciben datos en el intent tenemos una vista previa 

    		if (data != null) {
    			/*
    			  en el caso de una vista previa, obtenemos el extra data del intent y 
    			  lo mostramos en el ImageView
    			 */
    			if (data.hasExtra("data")) { 
    				ImageView iv = (ImageView)findViewById(R.id.imgView);
    				iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
    			}
    		
    		 // de lo contrario es una imagen completa
    		     			
    		} else {
    			/*
    			 a partir del nombre del archivo ya definido lo buscamos y creamos el bitmap
    			 para el ImageView
    			 */
    			ImageView iv = (ImageView)findViewById(R.id.imgView);
    			iv.setImageBitmap(BitmapFactory.decodeFile(name));
    			
    			 // para guardar la imagen en la galeria, utilizamos una conexion a un MediaScanner
    			 
    			new MediaScannerConnectionClient() {
    				private MediaScannerConnection msc = null; {
    					msc = new MediaScannerConnection(getApplicationContext(), this); msc.connect();
    				}
    				public void onMediaScannerConnected() { 
    					msc.scanFile(name, null);
    				}
    				public void onScanCompleted(String path, Uri uri) { 
    					msc.disconnect();
    				} 
    			};				
    		}
    
    	 // recibimos el URI de la imagen y construimos un Bitmap a partir de un stream de Bytes
    	 
    	} else if (requestCode == SELECT_PICTURE){
    		Uri selectedImage = data.getData();
    		InputStream is;
    		try {
    			is = getContentResolver().openInputStream(selectedImage);
    	    	BufferedInputStream bis = new BufferedInputStream(is);
    	    	Bitmap bitmap = BitmapFactory.decodeStream(bis);            
    	    	ImageView iv = (ImageView)findViewById(R.id.imgView);
    	    	iv.setImageBitmap(bitmap);						
    		} catch (FileNotFoundException e) {}
    	}
    }


    
}
