import re

def checkTInvariant(f, inv):
    exp = re.compile('(?<=Transicion disparada: ).+')
    aux = list()
    for line in f:
        transition = exp.search(line).group(0)
        if(transition in inv):
            aux.append(inv[transition])

    inv_size = len(inv)
    list_size = len(aux)
    status = True
    expected = list(range(inv_size))
    for i in range(0, list_size-(list_size%inv_size), inv_size):
        status &= aux[i:i+inv_size] == expected
    return status

t_inv_tren = {
    'BAJABAR_A':0,
	'LEVANTABAR_A':1,
	'LLEGATREN_B':2,
	'SALETREN_B':3,
	'LLEGATREN_C':4,
	'SALETREN_C':5,
	'BAJARBAR_C':6,
	'LEVANTARBAR_C':7,
	'LLEGATREN_D':8,
	'SALETREN_D':9,
	'LLEGATREN_A':10,
	'SALETREN_A':11
}

t_inv_autoa = {
    'LLEGA_AUTO_A':0,
    'AUTO_CRUZA_A':1,
    'AUTO_SE_VA_A':2
}

t_inv_autob = {
    'LLEGA_AUTO_B':0,
	'AUTO_CRUZA_B':1,
	'AUTO_SE_VA_B':2
}

inv = t_inv_tren

f = open("log.txt", "r")


print("T-Invariante de Tren:")
print(checkTInvariant(f, t_inv_tren))
print("T-Invariante de Auto-A")
print(checkTInvariant(f, t_inv_autoa))
print("T-Invariante de Auto-B")
print(checkTInvariant(f, t_inv_autob))
f.close()
