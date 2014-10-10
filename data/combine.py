import json,time,datetime,os.path,glob

DATA=[]

L_COUNT=0
T_COUNT=0

for file in glob.glob("*.json"):
    if (file!="makes.json" and file!='all_makes.json'):
         json_data=open(file)
         data=json.load(json_data)
         aList=data.get('car')
         L_COUNT=len(aList)
         T_COUNT=T_COUNT+L_COUNT
         print("Combining cars of make "+file[:len(file)-5]+" \t\t[car count="+str(L_COUNT)+"] \t[total count="+str(T_COUNT)+"]")
         DATA.extend(aList)

with open('all_makes.json',"w") as f:
    json.dump({'kbb':DATA},f,indent=2)

