# Task description

Explore the given data.  
Make a model to predict user conversion.
You have 5 hours for all.

# Installation

[Conda](https://docs.conda.io/en/latest/miniconda.html) was used to hold the working environment.  

I used an experimental environment solver `libmamba` because the classic solver takes a tremendous amount of time to resolve such an environment.  

First, install `libmamba`:  
```
conda update -n base conda
conda install -n base conda-libmamba-solver
```

After that, create an environment with an experimental solver:

```
conda create -n artefact_dmitrii_homework --experimental-solver=libmamba -c conda-forge notebook pandas-profiling ipywidgets pyarrow
conda activate artefact_dmitrii_homework
```

I used `pyarrow` to read the data because its alternative - `fastparquet` - was unable to do so.

Make a new Jupyter notebook with `jupyter notebook` command in the conda CLI.  
Extract data in a way that the `data` folder is located in the same folder where the notebook is saved.

# Steps and results

### Installation

Jupyter Notebook was chosen as a software for EDA because it's easy to test things in it.

First challenge was to make an environment that could read the data.  
`fastparquet` failed to read it. The installation of `pyarrow` failed to resolve with a standard environment solver.  
The solution was to install `pyarrow` with the experimental solver from `libmamba`.  

### Exploratory Data Analysis

My initial idea was to do EDA with the help of `pandas-profiling`, although it quickly became apparent that even its minimal configuration is unfit to work on a large dataset.  

After some search, I identified the given homework as [Google Analytics Sample](https://www.kaggle.com/datasets/bigquery/google-analytics-sample).  

When looking at the examples of EDA done on this dataset [link](https://www.kaggle.com/code/pavansanagapati/google-analytics-simple-exploration?scriptVersionId=5746639), I understood
that I will not be able to do EDA and Modeling in 5 hours, nor in 3 given days.  
To make it in 3 days, I need to go through the whole process at least once.  
To make it in 5 hours, I need to go through the whole process ten times.  
And that does not include the time to make a presentation, which can take days by itself.  
I did not go through it even once before, so the actual implementation would've taken around week of full-time effort focused on this homework.  

Therefore, I switched from the implementation of EDA to feature engineering and thinking on what model would be fitting for the task.

### Feature engineering

No information from the found analyses was used.  

Below are the core actions and ideas that I have regarding feature engineering:  

1. Flatten the objects in the sessions dataframe.  
2. Remove the heavily skewed columns because none of them can be inferred from the data in such a way that it is useful for training the model.  
3. Remove the rows with very scarce categories.  
4. The general idea is that model gets as much well-distributed information as possible before the curse-of-dimensionality. In other words, only a certain number of features is allowed as input.    
5. The conversion slider would depend on eCommerceAction.action_type. All action types would be mapped to the slider from 0 to 1 where 0 is not converted and 1 is converted. The conversion would mean the probability of getting revenue.  
6. To be useful, the visitStartTime needs to be converted to a value representing daytime in the given location. 
7. The date column needs to be converted to the day of the week.

### Modeling

Out of regression models, Logistic Regression fits the task - the task has many independent variables and one variable that indicates the occurrence of the conversion.  

An alternative approach would be to use a Fully Connected neural network to predict the probability of the user conversion.  
However, interpreting the changes, tracking of the progress, and the training itself of a neural network is much harder a regression model, so it depends on what the budget is and what the scope of the project is according to the lore of the task.

