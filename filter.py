#!/usr/bin/env python
# coding: utf-8

# In[4]:



import pandas as pd
import sys
import csv


# In[5]:

# with open(sys.argv[1], newline='') as f:
#     reader = csv.reader(f)
#     dataLabelsFrame = pd.read_csv (f)
#     dataLabelsFrame = "artist.csv"
#     dataLabelsFrame = dataLabelsFrame.drop(["URI"], axis=1)
#     writer = dataLabelsFrame.csv.writer(sys.stdout)
#     writer    

dataLabelsFrame = pd.read_csv("artist.csv", error_bad_lines=False, sep=',', header=None)
dataLabelsFrame = dataLabelsFrame.drop(["URI"], axis=1)
dataLabelsFrame = dataLabelsFrame.fillna(0)
dataLabelsFrame.to_csv("artist.csv", encoding='utf-8', index=False)
# dataLabelsFrame = dataLabelsFrame.drop(["URI"], axis=1)
# dataLabelsFrame.to_csv("artist.csv", encoding='utf-8', index=False)




# In[6]:



# In[7]:




# In[8]:


dataLabelsFrame.head(10)


# In[ ]:




