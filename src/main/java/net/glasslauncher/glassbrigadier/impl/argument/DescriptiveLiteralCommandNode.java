package net.glasslauncher.glassbrigadier.impl.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
public class DescriptiveLiteralCommandNode<S> extends LiteralCommandNode<S> implements DescriptiveNode {
    private final String description;
    private final String shortDescription;
    
    public DescriptiveLiteralCommandNode(String literal, String shortDescription, String description, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        super(literal, command, requirement, redirect, modifier, forks);
        this.description = description;
        this.shortDescription = shortDescription;
    }
}
