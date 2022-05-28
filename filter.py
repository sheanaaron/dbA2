#!/usr/bin/env python
# coding: utf-8

# In[4]:



import pandas as pd
import numpy as np


# In[5]:


dataLabelsFrame = pd.read_csv ('artist')


# In[6]:


dataLabelsFrame.head(10)


# In[7]:


dataLabelsFrame = dataLabelsFrame.drop(["URI"], axis=1)


# In[8]:


dataLabelsFrame.head()


# In[ ]:




