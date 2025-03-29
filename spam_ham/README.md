# Spam/Ham Email Classifier
## Project Overview
This project involves building a Logistic Regression model to classify emails as spam or ham (non-spam). The model uses various features, including specific keyword indicators, email length, number of exclamation marks, and the presence of HTML tags to make predictions. The goal was to explore feature engineering, model training, evaluation, and threshold adjustment to achieve a balanced performance in classifying spam emails.

## Technologies Used
Python

Libraries:

Pandas (for data manipulation)

Scikit-learn (for building and evaluating the model)

NumPy (for numerical operations)

Matplotlib, Seaborn (for visualization)

## Dataset
The dataset consists of labeled emails where each email is marked as either ham (0) or spam (1). The data includes the email content and a label indicating whether the email is spam or not.

Features Used in the Model:

Specific Keywords: Presence of words such as 'business', 'money', 'offer', 'please', 'reply'.

Email Length: Total number of words in the email.

Exclamation Marks: Number of exclamation points in the email.

HTML Tags: Presence of HTML tags such as 'body', 'html', 'img', 'div', 'span'.

## Results

After training the model, the following metrics were achieved:

Testing Accuracy: 86.8%

Testing Precision: 83.65%

Testing Recall: 63.97%

Testing F1 Score: 0.7250

## Key Insights
There is a trade-off between precision and recall. Adjusting the decision threshold helped improve recall at the cost of a small decrease in precision.

The model was able to achieve a reasonable balance between these metrics, though recall could still be improved further by tuning the threshold or exploring other algorithms. For the regular user, though, this would not be particularly important.

## License
This project is licensed under the MIT License.
