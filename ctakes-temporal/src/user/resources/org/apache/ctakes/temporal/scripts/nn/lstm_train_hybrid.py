#!/usr/bin/env python

import numpy as np
np.random.seed(1337)
import sys
import os.path
import dataset_hybrid
from keras.utils.np_utils import to_categorical
from keras.optimizers import RMSprop
from keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential
from keras.layers import Merge
from keras.layers.core import Dense, Activation
from keras.layers import LSTM
from keras.layers.embeddings import Embedding
import pickle

def main(args):
    if len(args) < 1:
        sys.stderr.write("Error - one required argument: <data directory>\n")
        sys.exit(-1)
    working_dir = args[0]
    data_file = os.path.join(working_dir, 'training-data.liblinear')

    # learn alphabet from training data
    provider = dataset_hybrid.DatasetProvider(data_file)
    # now load training examples and labels
    train_x1, train_x2, train_y = provider.load(data_file)
    # turn x and y into numpy array among other things
    maxlen = max([len(seq) for seq in train_x1])
    classes = len(set(train_y))

    train_x1 = pad_sequences(train_x1, maxlen=maxlen)
    train_x2 = pad_sequences(train_x2, maxlen=maxlen)
    train_y = to_categorical(np.array(train_y), classes)

    pickle.dump(maxlen, open(os.path.join(working_dir, 'maxlen.p'),"wb"))
    pickle.dump(provider.word2int, open(os.path.join(working_dir, 'word2int.p'),"wb"))
    pickle.dump(provider.tag2int, open(os.path.join(working_dir, 'tag2int.p'),"wb"))
    pickle.dump(provider.label2int, open(os.path.join(working_dir, 'label2int.p'),"wb"))

    print 'train_x1 shape:', train_x1.shape
    print 'train_x2 shape:', train_x2.shape
    print 'train_y shape:', train_y.shape

    branches = [] # models to be merged
    train_xs = [] # train x for each branch

    branch1 = Sequential()
    branch1.add(Embedding(len(provider.word2int),
                          300,
                          input_length=maxlen,
                          dropout=0.25))
    branch1.add(LSTM(128,
                dropout_W = 0.20,
                dropout_U = 0.20))

    branches.append(branch1)
    train_xs.append(train_x1)

    branch2 = Sequential()
    branch2.add(Embedding(len(provider.tag2int),
                          300,
                          input_length=maxlen,
                          dropout=0.25))
    branch2.add(LSTM(128,
                dropout_W = 0.20,
                dropout_U = 0.20))

    branches.append(branch2)
    train_xs.append(train_x2)

    model = Sequential()
    model.add(Merge(branches, mode='concat'))
    model.add(Dense(classes))
    model.add(Activation('softmax'))

    optimizer = RMSprop(lr=0.001, rho=0.9, epsilon=1e-08)
    model.compile(loss='categorical_crossentropy',
                  optimizer=optimizer,
                  metrics=['accuracy'])
    model.fit(train_xs,
              train_y,
              nb_epoch=25,
              batch_size=50,
              verbose=0,
              validation_split=0.1)

    json_string = model.to_json()
    open(os.path.join(working_dir, 'model_0.json'), 'w').write(json_string)
    model.save_weights(os.path.join(working_dir, 'model_0.h5'), overwrite=True)
    sys.exit(0)

if __name__ == "__main__":
    main(sys.argv[1:])
