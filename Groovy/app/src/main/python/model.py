import warnings
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from tqdm import tqdm

sns.set()

data = pd.read_csv("spotify.csv")
data.head()

sns.set()

data.isnull().sum()

df = data.drop(columns = ['id'])
df.corr()

#data transformation
#normalizing the dataset
from sklearn.preprocessing import MinMaxScaler
datatypes = ['int16','int32','int64','float16','float32','float64']  #normalizing only numerical columns
normalization = data.select_dtypes(include=datatypes)
for col in normalization.columns:
    MinMaxScaler(col)

#K Means clustering algo to differentiate songs from different categories   #collaborative filtering
from sklearn.cluster import KMeans
kmeans = KMeans(n_clusters = 10)
features = kmeans.fit_predict(normalization)
data['features'] = features
MinMaxScaler(data['features'])

#Recommendation engine
class Recommendations():
    def __init__(self, dataset):
        self.dataset = dataset;
    def recommend(self,songs,amount=1):
        distance = []
        song = self.dataset[(self.dataset.name.str.lower() == songs.lower())].head(1).values[0]
        rec = self.dataset[self.dataset.name.str.lower() != songs.lower()]
        for songs in tqdm(rec.values):
            d = 0
            for col in np.arange(len(rec.columns)):
                if not col in [1,6,12,14,18]:
                    d = d + np.absolute(float(song[col]) - float(songs[col]))
            distance.append(d)
        rec['distance'] = distance
        rec = rec.sort_values('distance')
        columns = ['id','artists','name']
        return rec[columns][:amount]

def songModel(songName : str):
    try:
        recomm = Recommendations(data)
        df = pd.DataFrame(recomm.recommend(songName,10))
        # df.to_csv('result.csv')
        df.to_json('result.json')
    except:
        print('No song Found!')