/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.feel.analysis.semantics.type;

import java.util.*;
import java.util.stream.Collectors;

import static com.gs.dmn.feel.analysis.semantics.type.AnyType.ANY;

public class ItemDefinitionType extends NamedType implements CompositeDataType {
    public static final Type ANY_ITEM_DEFINITION = new ItemDefinitionType("");

    private final Map<String, Type> members = new LinkedHashMap<>();
    private final Map<String, List<String>> aliases = new LinkedHashMap<>();
    private final String modelName;

    public ItemDefinitionType(String name) {
        this(name, null);
    }

    public ItemDefinitionType(String name, String modelName) {
        super(name);
        this.modelName = modelName;
    }

    public String getModelName() {
        return this.modelName;
    }

    @Override
    public ItemDefinitionType addMember(String name, List<String> aliases, Type type) {
        this.members.put(name, type);
        this.aliases.put(name, aliases);
        return this;
    }

    @Override
    public Set<String> getMembers() {
        return members.keySet();
    }

    @Override
    public Type getMemberType(String member) {
        return members.get(member);
    }

    @Override
    public List<String> getAliases(String name) {
        List<String> aliases = this.aliases.get(name);
        return aliases == null ? new ArrayList<>() : aliases;
    }

    @Override
    public boolean equivalentTo(Type other) {
        if (other instanceof ContextType) {
            Set<String> thisNames = this.getMembers();
            Set<String> otherNames = ((ContextType) other).getMembers();
            if (!thisNames.equals(otherNames)) {
                return false;
            }
            for (String name : thisNames) {
                Type thisType = this.getMemberType(name);
                Type otherType = ((ContextType) other).getMemberType(name);
                if (!thisType.equivalentTo(otherType)) {
                    return false;
                }
            }
            return true;
        } else if (other instanceof ItemDefinitionType) {
            Set<String> thisNames = this.getMembers();
            Set<String> otherNames = ((ItemDefinitionType) other).getMembers();
            if (!thisNames.equals(otherNames)) {
                return false;
            }
            for (String name : thisNames) {
                Type thisType = this.getMemberType(name);
                Type otherType = ((ItemDefinitionType) other).getMemberType(name);
                if (!thisType.equivalentTo(otherType)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public ContextType toContextType() {
        ContextType contextType = new ContextType();
        members.entrySet().forEach(
                entry -> contextType.addMember(entry.getKey(), aliases.get(entry.getKey()), entry.getValue())
        );
        return contextType;
    }

    @Override
    public boolean conformsTo(Type other) {
        if (other == ANY) {
            return true;
        }
        if (other instanceof ContextType) {
            Set<String> thisNames = this.getMembers();
            Set<String> otherNames = ((ContextType) other).getMembers();
            if (!thisNames.containsAll(otherNames)) {
                return false;
            }
            for (String name : otherNames) {
                Type thisType = this.getMemberType(name);
                Type otherType = ((ContextType) other).getMemberType(name);
                if (!thisType.conformsTo(otherType)) {
                    return false;
                }
            }
            return true;
        } else if (other instanceof ItemDefinitionType) {
            Set<String> thisNames = this.getMembers();
            Set<String> otherNames = ((ItemDefinitionType) other).getMembers();
            if (!thisNames.containsAll(otherNames)) {
                return false;
            }
            for (String name : otherNames) {
                Type thisType = this.getMemberType(name);
                Type otherType = ((ItemDefinitionType) other).getMemberType(name);
                if (!thisType.conformsTo(otherType)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValid() {
        if (members.isEmpty()) {
            return false;
        }
        return members.values().stream().allMatch(t -> t.isValid() && t != AnyType.ANY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDefinitionType that = (ItemDefinitionType) o;

        if (!members.equals(that.members)) return false;
        return aliases.equals(that.aliases);

    }

    @Override
    public int hashCode() {
        int result = members.hashCode();
        result = 31 * result + aliases.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String members = this.members.entrySet().stream().map(e -> String.format("%s = %s", toList(e.getKey(), this.aliases.get(e.getKey())), e.getValue())).collect(Collectors.joining(", "));
        return String.format("ItemDefinitionType(%s)", members);
    }

    private String toList(String name, List<String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return name;
        }
        return name + ", " + String.join(", ", aliases);
    }
}
