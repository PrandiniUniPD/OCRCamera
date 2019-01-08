package unipd.se18.ocrcamera.forum;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Describes an object that will be used to send network requests
 * @author Leonardo Rossi g2
 */
public class RequestManager
{
    /**
     * ****************+******
     * **   NESTED CLASSES  **
     * ***********************
     */

    /**
     * Defines the requests that can be sent to the database
     */
    public enum RequestType
    {
        LOGIN("l"),
        GET_POSTS("gPosts"),
        ADD_POST("adPost"),
        ANSWER_POST("awPost");

        public String value;

        /**
         * Defines an object of type RequestType
         * @param value The value that will be add to the network request's url for the specified request type
         */
        RequestType(String value){ this.value = value; }

    }

    /**
     * Defines the object that is used to compose a network request
     */
    public static class Parameter
    {
        String key;
        String value;

        /**
         * @param key The key that identifies the parameter into the network request url
         * @param value The value of the specified parameters
         */
        public Parameter(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return key + "=" + value;
        }
    }

    /**
     * Defines the object that is passed to the async network task
     */
    public class TaskObj
    {
        Context context;
        ArrayList<Parameter> parameters;

        /**
         * @param context The activity responsible of the network request
         * @param parameters The parameters for the network request
         */
        TaskObj(Context context, ArrayList<Parameter> parameters)
        {
            this.context = context;
            this.parameters = parameters;
        }

        @Override
        public String toString()
        {
            String description = "";

            for (Parameter p : parameters){ description += p.toString() + "&"; }

            return description;
        }
    }

    /**
     * Defines the object which will be responsible to send a network request
     */
    private class Task extends AsyncTask<TaskObj, Void, String>
    {

        private final String SERVER = "elementiunipd.rf.gd";
        private final String REQUEST_URL = "http://" + SERVER + "/forum/index.php?";

        @Override
        protected String doInBackground(TaskObj... params)
        {
            TaskObj obj = params[0];

            //Network request's url composition
            String request = REQUEST_URL + obj.toString();

            try
            {
                //Open a connection to the specified url with the chance to pass information to the
                //destination server and retrieve a response from it
                URL rUrl = new URL(request);
                URLConnection connection = rUrl.openConnection();
                //Enabling the chance to pass and retrieve information
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Network request's response
                InputStream inputStream = connection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String response = "";
                String line = buffer.readLine();

                while(line != null)
                {
                    response += line;
                    line = buffer.readLine();
                }

                buffer.close();
                inputStream.close();
                
                Log.d("#######", response);

                return response;
            }
            catch (MalformedURLException e)
            {
                if (listener != null) { listener.onConnectionFailed(e.getMessage()); }
            }
            catch (IOException e)
            {
                if (listener != null) { listener.onParametersSendingFailed(e.getMessage()); }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            if (listener != null) { listener.onRequestFinished(s); }
        }
    }


    /**
     *  ****************
     *  **  LISTENER  **
     *  ****************
     */

    /**
     * An interface that represents the events to which this class can respond to
     */
    public interface RequestManagerListener
    {
        /**
         * This method is fired when a specific network request is finished
         * @param response The network request's response
         */
        void onRequestFinished(String response);

        /**
         * This method is fired when an error occurs while establishing the connection to a
         * specified URL
         * @param message The error message
         */
        void onConnectionFailed(String message);

        /**
         * This method is fired when an error occurs while passing parameter into the network request
         * @param message The error message
         */
        void onParametersSendingFailed(String message);
    }

    /**
     * *****************
     * ** ATTRIBUTES  **
     * *****************
     */

    public RequestManagerListener listener;

    /**
     * ***********************
     * **   PUBLIC METHODS  **
     * ***********************
     */

    /**
     * Sets the specified listener implementation to the object
     * @param listener The specified listener implementation
     */
    public void setOnRequestFinishedListener(RequestManagerListener listener){ this.listener = listener; }

    /**
     * Sends a request to the database with the specified parameters
     * @param context The activity from which the request starts
     * @param parameters The network request's parameters
     */
    public void sendRequest(Context context, ArrayList<Parameter> parameters)
    {
        TaskObj tObj = new TaskObj(context, parameters);
        Task task = new Task();
        task.execute(tObj);
    }
}
