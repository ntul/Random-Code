import random
import string


#random.seed(6666)

class Word:
   def  __init__(self, *args):
        self.arr = []
        for i in range (5):
            self.arr.append(random.choice(string.ascii_lowercase))
        self.arr.append(0)

class Pool:
    generation=0
    found = False
    size=0
    def  __init__(self, size):
        self.size=size
        self.arr = []
        for i in range (size):
            x=Word()
            self.arr.append(x.arr)

def fitness(cl):    
    for i in cl.arr:
        if (i[5]!=0): continue
        if (i[0]=='h'): i[5]+=1
        if (i[1]=='e' in i): i[5]+=1
        if (i[2]=='l' in i): i[5]+=1
        if (i[3]=='l' in i): i[5]+=1
        if (i[4]=='o' in i): i[5]+=1
        if(i[5]==5): cl.found=True    

def aSort(cl):
    cl.arr=sorted(cl.arr,key=lambda x:x[5],reverse=True)

def cross(cl, offspring, parent1, parent2):
    cl.arr[offspring][0]=cl.arr[parent1][0]
    cl.arr[offspring][1]=cl.arr[parent1][1]
    cl.arr[offspring][2]=cl.arr[parent2][2]
    cl.arr[offspring][3]=cl.arr[parent2][3]
    cl.arr[offspring][4]=cl.arr[parent2][4]
    cl.arr[offspring][5]=0
    mutation(cl, offspring)

def crossover(cl):
    cross(cl,cl.size-1,0,1)
    cross(cl,cl.size-2,2,3)
    cross(cl,cl.size-3,4,5)
    cross(cl,cl.size-4,6,7)
    cross(cl,cl.size-5,8,9)
    cross(cl,cl.size-6,10,11)
    
def mutation(cl,offspring):
    i=random.randint(0,4)
    cl.arr[offspring][i]=random.choice(string.ascii_lowercase)
        
p = Pool(100)

while(1==1):
    p.generation+=1
    fitness(p)    
    aSort(p)
    print(p.arr[0])
    if(p.found==True): break;
    crossover(p)

print('Population size: {0}'.format(p.size))
print('Generations: {0}'.format(p.generation))
#print(p.found)
#print(p.arr)

#input('Press any key to continue ...')
