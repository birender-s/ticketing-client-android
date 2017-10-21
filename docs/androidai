# Configuring AI into App 
To set up AIService and AIConfiguration 

## Step 1. add below files to your build.gradle

```gradle
 compile 'ai.api:sdk:2.0.6@aar'
 compile ('ai.api:libai:1.4.8') {
 exclude module: 'log4j-core'
 }
 ```
 
 ## Step 2. Initialize config and AI with AI CONFIGURATION TOKEN
 
```gradle
//Method to initialize AI
    private void initializeAI() {
        aiConfiguration = new AIConfiguration(Constant.AI_CONFIGURATION_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, aiConfiguration);
        aiService.setListener(this);
    }
```

## Step 3. Implement AIListener interface and overide callback methods to handle response received from Dialogflow

... Implement AIListener and overide methods
public class AdminAgentActivity extends AppCompatActivity implements AIListener{

  @Override
    public void onResult(AIResponse response) {
     // Add functionality
  }
}
...  
